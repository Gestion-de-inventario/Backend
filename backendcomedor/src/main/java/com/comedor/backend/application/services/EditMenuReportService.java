package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.EditMenuReportUseCase;
import com.comedor.backend.application.ports.in.RegistrarTransaccionUseCase;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.domain.exceptions.StockInsuficienteException;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.FuenteTransaccion;
import com.comedor.backend.domain.model.enums.TipoMovimiento;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditMenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransaccionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductoFaltanteResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResultadoConsumoStock;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class EditMenuReportService implements EditMenuReportUseCase {
    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final ProductRepositoryPort productRepository;
    private final InventoryLotRepositoryPort inventoryLotRepository;
    private final PersonRepositoryPort personRepositoryPort;
    private final MenuReportMapper mapper;

    private final RegistrarTransaccionUseCase registrarTransaccionUseCase;
    private final CurrentUserService currentUserService;

    public EditMenuReportService(MenuReportRepositoryPort menuReportRepositoryPort, DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepository, InventoryLotRepositoryPort inventoryLotRepository, PersonRepositoryPort personRepositoryPort, MenuReportMapper mapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.productRepository = productRepository;
        this.inventoryLotRepository = inventoryLotRepository;
        this.personRepositoryPort = personRepositoryPort;
        this.mapper = mapper;
        this.registrarTransaccionUseCase = registrarTransaccionUseCase;
        this.currentUserService = currentUserService;
    }


    @Override
    @Transactional
    public ReporteMenuResponseDTO editMenuReport(
            Integer id,
            EditMenuReportRequestDTO request
    ) {

        MenuReport reporte =
                menuReportRepositoryPort.findById(id);

        boolean menuChanged =
                !Objects.equals(
                        reporte.getDishMenu().getId(),
                        request.getDishMenuId()
                );

        boolean quantityChanged =
                !Objects.equals(
                        reporte.getQuantityPrepared(),
                        request.getQuantityPrepared()
                );

        boolean cooksChanged =
                !new HashSet<>(reporte.getCooks())
                        .equals(new HashSet<>(request.getCooks()));

        // =========================
        // NO HUBO CAMBIOS
        // =========================
        if (!menuChanged && !quantityChanged && !cooksChanged) {
            return mapper.toDto(reporte);
        }

        // =========================
        // SOLO CAMBIARON COCINERAS
        // =========================
        if (!menuChanged && !quantityChanged && cooksChanged) {

            reporte.setCooks(request.getCooks());

            return mapper.toDto(
                    menuReportRepositoryPort.save(reporte)
            );
        }

        // =========================
        // MENU O CANTIDAD CAMBIARON
        // REQUIERE VALIDAR BENEFICIARIOS
        // =========================
        if (!reporte.getBeneficiaryControls().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede editar menú con beneficiarios asociados"
            );
        }

        // =========================
        // MENÚ FINAL
        // =========================
        DishMenu finalDishMenu = reporte.getDishMenu();

        if (menuChanged) {
            finalDishMenu =
                    dishMenuRepositoryPort.findById(
                            request.getDishMenuId()
                    );
        }

        BigDecimal newQty =
                BigDecimal.valueOf(
                        request.getQuantityPrepared()
                );

        // =========================
        // VALIDAR STOCK
        // =========================

        if (menuChanged || quantityChanged) {

            validarStockEdicion(
                    reporte,
                    finalDishMenu,
                    newQty
            );
        }

        // =========================
        // REVERTIR STOCK Y LOTES
        // =========================
        revertirStock(reporte);

        // =========================
        // APLICAR NUEVO CONSUMO
        // =========================
        ResultadoConsumoStock resultado =
                aplicarNuevoStock(
                        finalDishMenu,
                        newQty
                );

        // =========================
        // ACTUALIZAR ENTIDAD
        // =========================
        reporte.setDishMenu(finalDishMenu);
        reporte.setQuantityPrepared(
                request.getQuantityPrepared()
        );
        reporte.setQuantityRemaining(
                request.getQuantityPrepared()
        );
        reporte.setCooks(
                request.getCooks()
        );

        for (StockMovement movement : resultado.getMovimientos()) {
            movement.setMenuReport(reporte);
        }

        reporte.setStockMovements(
                resultado.getMovimientos()
        );
        reporte.setTotalSpent(
                resultado.getTotalSpent()
        );

        // pendiente:
        // registrar transacción MODIFICACION

        return mapper.toDto(
                menuReportRepositoryPort.save(reporte)
        );
    }


    private void validarStockEdicion(
            MenuReport reporteActual,
            DishMenu nuevoMenu,
            BigDecimal nuevaCantidad
    ) {

        List<ProductoFaltanteResponseDTO> faltantes =
                new ArrayList<>();

        for (DishSupply supply : nuevoMenu.getSupplies()) {

            BigDecimal requeridoNuevo =
                    supply.getQuantityNeeded()
                            .multiply(nuevaCantidad);

            BigDecimal liberado = BigDecimal.ZERO;

            DishMenu menuActual = reporteActual.getDishMenu();

            DishSupply supplyActual =
                    menuActual.getSupplies()
                            .stream()
                            .filter(s ->
                                    Objects.equals(
                                            s.getProduct().getId(),
                                            supply.getProduct().getId()
                                    )
                            )
                            .findFirst()
                            .orElse(null);

            if (supplyActual != null) {

                liberado =
                        supplyActual.getQuantityNeeded()
                                .multiply(
                                        BigDecimal.valueOf(
                                                reporteActual.getQuantityPrepared()
                                        )
                                );
            }

            Product product = supply.getProduct();

            BigDecimal stockDisponible =
                    product.getStock().add(liberado);

            if (stockDisponible.compareTo(requeridoNuevo) < 0) {

                faltantes.add(
                        new ProductoFaltanteResponseDTO(
                                product.getId(),
                                product.getName(),
                                product.getUnit(),
                                requeridoNuevo.subtract(stockDisponible)
                        )
                );
            }
        }

        if (!faltantes.isEmpty()) {
            throw new StockInsuficienteException(faltantes);
        }
    }
    private void revertirStock(MenuReport reporte) {

        BigDecimal qty =
                BigDecimal.valueOf(reporte.getQuantityPrepared());

        for (StockMovement m : reporte.getStockMovements()) {

            InventoryLot lote = m.getInventoryLot();

            lote.setRemainingQuantity(
                    lote.getRemainingQuantity().add(m.getQuantityUsed())
            );

            inventoryLotRepository.update(lote);
        }

        for (DishSupply supply : reporte.getDishMenu().getSupplies()) {

            BigDecimal total =
                    supply.getQuantityNeeded().multiply(qty);

            Product product = supply.getProduct();

            product.setStock(
                    product.getStock().add(total)
            );

            productRepository.updateStock(product);
        }
    }

    private ResultadoConsumoStock aplicarNuevoStock(
            DishMenu menu,
            BigDecimal qty
    ) {

        List<StockMovement> movimientos = new ArrayList<>();

        BigDecimal totalSpent = BigDecimal.ZERO;

        for (DishSupply supply : menu.getSupplies()) {

            BigDecimal requerido =
                    supply.getQuantityNeeded().multiply(qty);

            BigDecimal pendiente = requerido;

            Product product = supply.getProduct();

            List<InventoryLot> lotes =
                    inventoryLotRepository.findAvailableByProduct(
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
                movement.setUnitCost(lote.getUnitCost());

                BigDecimal totalCost =
                        consumido.multiply(
                                lote.getUnitCost()
                        );

                movement.setTotalCost(totalCost);

                movement.setMovementDate(PeruTime.now());

                movimientos.add(movement);

                totalSpent =
                        totalSpent.add(totalCost);

                lote.setRemainingQuantity(
                        lote.getRemainingQuantity()
                                .subtract(consumido)
                );

                inventoryLotRepository.update(lote);

                pendiente =
                        pendiente.subtract(consumido);
            }
            // Descontar del stock agregado del producto
            product.setStock(
                    product.getStock().subtract(requerido)
            );

            productRepository.updateStock(product);
        }

        return new ResultadoConsumoStock(
                movimientos,
                totalSpent
        );
    }


  /*  private void registrarMovimiento(
            Integer usuarioId,
            Integer productoId,
            BigDecimal amount,
            TipoMovimiento tipo,
            FuenteTransaccion source
    ) {
        TransaccionRequestDTO dto = new TransaccionRequestDTO();

        dto.setAmount(amount);
        dto.setProductId(productoId);
        dto.setUserId(usuarioId);
        dto.setType(tipo);
        dto.setSource(source);

        registrarTransaccionUseCase.registrarTransaccion(dto);
    }*/
}
