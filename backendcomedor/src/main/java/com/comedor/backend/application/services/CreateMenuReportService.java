package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.CreateMenuReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.domain.exceptions.DateException;
// Faltará importar tus clases de Excepciones personalizadas y StockMovement
import com.comedor.backend.domain.exceptions.InsufficientStockException;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.StatusMenuReport;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.MenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MissingProductResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateMenuReportService implements CreateMenuReportUseCase {

    private final MenuReportRepositoryPort repository;
    private final DishMenuRepositoryPort dishMenuRepository;
    private final ProductRepositoryPort productRepository;
    private final InventoryLotRepositoryPort inventoryLotRepository;
    private final MenuReportMapper mapper;

    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final CurrentUserService currentUserService;

    public CreateMenuReportService(MenuReportRepositoryPort repository,
                                   DishMenuRepositoryPort dishMenuRepository, ProductRepositoryPort productRepository,
                                   InventoryLotRepositoryPort inventoryLotRepository,
                                   MenuReportMapper mapper
                                  , RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService) {
        this.repository = repository;
        this.dishMenuRepository = dishMenuRepository;
        this.productRepository = productRepository;
        this.inventoryLotRepository = inventoryLotRepository;
        this.mapper = mapper;

        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public MenuReportResponseDTO crearReporteMenu(MenuReportRequestDTO request) {
        // CUEVA Corrección BU-009 , regresar la restricción una vez presentado en taller.
        /*if (repository.existByDate(PeruTime.today())) {
            throw new ReporteMenuYaExistente("Ya existe un reporte menu para hoy");
        }*/

        DishMenu dishMenu = dishMenuRepository.findById(request.getDishMenuId());
        List<MissingProductResponseDTO> productosFaltantes = new ArrayList<>();
        Integer usuarioId = currentUserService.getCurrentUser().getId();
        for (DishSupply supply : dishMenu.getSupplies()) {

            BigDecimal requerido = supply.getQuantityNeeded()
                    .multiply(BigDecimal.valueOf(request.getQuantityPrepared()));

            Product product = supply.getProduct();

            if (product.getStock().compareTo(requerido) < 0) {

                BigDecimal faltante =
                        requerido.subtract(product.getStock());

                productosFaltantes.add(new MissingProductResponseDTO(product.getId(),product.getName(),product.getUnit(),faltante));
            }
        }

        if (!productosFaltantes.isEmpty()) {
            throw new InsufficientStockException(productosFaltantes);
        }

        for (DishSupply supply : dishMenu.getSupplies()) {

            BigDecimal requerido = supply.getQuantityNeeded()
                    .multiply(BigDecimal.valueOf(request.getQuantityPrepared()));

            Product product = supply.getProduct();

            product.setStock(
                    product.getStock().subtract(requerido)
            );


            registrarMovimiento(
                    usuarioId,
                    product.getId(),
                    product.getName(),
                    requerido,
                    product.getStock(),
                    // CUEVA
                    // PeruTime.now()
                    request.getCreateDate().atStartOfDay(),
                    TransactionReferenceType.INGREDIENTE,
                    MovementType.SALIDA

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

                //Refactor fecha CUEVA
                /* movement.setMovementDate(
                        PeruTime.now()
                );*/
                movement.setMovementDate(request.getCreateDate().atStartOfDay());
                //

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
        // Refactor de fecha CUEVA
        //reporte.setDate(PeruTime.today());
        if(request.getCreateDate().isBefore(PeruTime.today())  )
        {
            throw new DateException("Error al crear orden : La fecha de creación no puede ser menor a la actual ");
        }

        reporte.setDate(request.getCreateDate());
        //
        reporte.setCooks(request.getCooks());
        reporte.setDishMenu(dishMenu);
        reporte.setQuantityPrepared(request.getQuantityPrepared());
        reporte.setQuantityRemaining(request.getQuantityPrepared());
        reporte.setStockMovements(movimientos);
        reporte.setTotalSpent(totalSpent);
        reporte.setStatus(StatusMenuReport.ABIERTO);

        //Registrando stock de menu (BOM)
        registrarMovimiento(
                usuarioId,
                null,
                dishMenu.getName(),
                BigDecimal.valueOf(request.getQuantityPrepared()),
                BigDecimal.ZERO,
                //Volver a crear solo hoy
                // CUEVA
                // PeruTime.now()
                request.getCreateDate().atStartOfDay(),
                TransactionReferenceType.MENU,
                MovementType.ENTRADA
                );

        return mapper.toDto(repository.create(reporte));
    }

    private void registrarMovimiento(
            Integer userId,
            Integer referenceId,
            String itemName,
            BigDecimal amount,
            BigDecimal currentStock,
            LocalDateTime localDateTime,
            TransactionReferenceType referenceType,
            MovementType movementType
    ) {
        TransactionRequestDTO dto = new TransactionRequestDTO();

        dto.setReferenceType(referenceType);
        if(referenceId != null)
        {
            dto.setReferenceId(referenceId);
        }
        dto.setItemName(itemName);
        dto.setType(movementType);
        dto.setAmount(amount);
        dto.setCurrentStock(currentStock);
        dto.setSource(TransactionSource.INVENTARIO);
        dto.setUserId(userId);
        dto.setDateTime(localDateTime);
        registerTransactionUseCase.registrarTransaccion(dto);
    }
}