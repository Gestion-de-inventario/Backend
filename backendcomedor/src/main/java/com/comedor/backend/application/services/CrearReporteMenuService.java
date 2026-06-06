package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.CrearReporteMenuUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.domain.exceptions.ReporteMenuYaExistente;
// Faltará importar tus clases de Excepciones personalizadas y StockMovement
import com.comedor.backend.domain.exceptions.StockInsuficienteException;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.EstadoReporteMenu;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoFaltanteResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.StockInsuficienteResponseDTO;
import org.springframework.transaction.annotation.Transactional; // MUY IMPORTANTE

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CrearReporteMenuService implements CrearReporteMenuUseCase {

    private final MenuReportRepositoryPort repository;
    private final DishMenuRepositoryPort dishMenuRepository;
    private final ProductRepositoryPort productRepository;
    private final InventoryLotRepositoryPort inventoryLotRepository;
    private final MenuReportMapper mapper;

    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;
    private final CurrentUserService currentUserService;

    public CrearReporteMenuService(MenuReportRepositoryPort repository,
                                   DishMenuRepositoryPort dishMenuRepository, ProductRepositoryPort productRepository,
                                   InventoryLotRepositoryPort inventoryLotRepository,
                                   MenuReportMapper mapper
                                  , RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService) {
        this.repository = repository;
        this.dishMenuRepository = dishMenuRepository;
        this.productRepository = productRepository;
        this.inventoryLotRepository = inventoryLotRepository;
        this.mapper = mapper;

        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public ReporteMenuResponseDTO crearReporteMenu(ReporteMenuRequestDTO request) {
        if (repository.existByDate(LocalDate.now())) {
            throw new ReporteMenuYaExistente("Ya existe un reporte menu para hoy");
        }

        DishMenu dishMenu = dishMenuRepository.findById(request.getDishMenuId());
        List<ProductoFaltanteResponseDTO> productosFaltantes = new ArrayList<>();

        for (DishSupply supply : dishMenu.getSupplies()) {

            BigDecimal requerido = supply.getQuantityNeeded()
                    .multiply(BigDecimal.valueOf(request.getQuantityPrepared()));

            Product product = supply.getProduct();

            if (product.getStock().compareTo(requerido) < 0) {

                BigDecimal faltante =
                        requerido.subtract(product.getStock());

                productosFaltantes.add(new ProductoFaltanteResponseDTO(product.getId(),product.getName(),product.getUnit(),faltante));
            }
        }

        if (!productosFaltantes.isEmpty()) {
            throw new StockInsuficienteException(productosFaltantes);
        }

        for (DishSupply supply : dishMenu.getSupplies()) {

            BigDecimal requerido = supply.getQuantityNeeded()
                    .multiply(BigDecimal.valueOf(request.getQuantityPrepared()));

            Product product = supply.getProduct();

            product.setStock(
                    product.getStock().subtract(requerido)
            );

            Integer usuarioId = currentUserService.getCurrentUser().getId();
            registrarMovimiento(
                    usuarioId,
                    product.getId(),
                    requerido,
                    TipoMovimiento.SALIDA
            );
            productRepository.updateStock(product);
        }

        List<StockMovement> movimientos = new ArrayList<>();

        BigDecimal totalSpent = BigDecimal.ZERO;

        for (DishSupply supply : dishMenu.getSupplies()) {

            BigDecimal pendiente =
                    supply.getQuantityNeeded()
                            .multiply(
                                    BigDecimal.valueOf(
                                            request.getQuantityPrepared()
                                    )
                            );

            List<InventoryLot> lotes =
                    inventoryLotRepository
                            .findAvailableByProduct(
                                    supply.getProduct().getId()
                            );

            for (InventoryLot lote : lotes) {

                if (pendiente.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal consumido =
                        pendiente.min(
                                lote.getRemainingQuantity()
                        );

                StockMovement movement =
                        new StockMovement();

                movement.setInventoryLot(lote);

                movement.setQuantityUsed(consumido);

                movement.setUnitCost(
                        lote.getUnitCost()
                );

                movement.setTotalCost(
                        consumido.multiply(
                                lote.getUnitCost()
                        )
                );

                movement.setMovementDate(
                        LocalDateTime.now()
                );

                movimientos.add(movement);

                totalSpent =
                        totalSpent.add(
                                movement.getTotalCost()
                        );

                lote.setRemainingQuantity(
                        lote.getRemainingQuantity()
                                .subtract(consumido)
                );

                inventoryLotRepository.update(lote);

                pendiente =
                        pendiente.subtract(consumido);
            }

            if (pendiente.compareTo(BigDecimal.ZERO) > 0) {

                throw new RuntimeException(
                        "Inconsistencia de inventario para "
                                + supply.getProduct().getName()
                );
            }
        }


        MenuReport reporte = new MenuReport();
        reporte.setId(null);
        reporte.setDate(LocalDate.now());
        reporte.setCooks(request.getCooks());
        reporte.setDishMenu(dishMenu);
        reporte.setQuantityPrepared(request.getQuantityPrepared());
        reporte.setQuantityRemaining(request.getQuantityPrepared());
        reporte.setStockMovements(movimientos);
        reporte.setTotalSpent(totalSpent);
        reporte.setStatus(EstadoReporteMenu.ABIERTO);

        return mapper.toDto(repository.create(reporte));
    }

    private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            TipoMovimiento tipo
    ) {
        TransaccionRequestDTO dto = new TransaccionRequestDTO();

        dto.setAmount(amount);
        dto.setProductId(productoId);
        dto.setUserId(usuarioId);
        dto.setType(tipo);

        registrarTransaccionUseCase.registrarTransaccion(dto);
    }
}