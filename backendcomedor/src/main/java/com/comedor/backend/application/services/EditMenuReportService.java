package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.EditMenuReportUseCase;
import com.comedor.backend.application.ports.in.RegisterTransactionUseCase;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.domain.exceptions.InsufficientStockException;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditMenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TransactionRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MissingProductResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.StockConsumptionResultResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class EditMenuReportService implements EditMenuReportUseCase {
    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final DishMenuRepositoryPort dishMenuRepositoryPort;
    private final ProductRepositoryPort productRepository;
    private final InventoryLotRepositoryPort inventoryLotRepository;
    private final UserRepositoryPort userRepositoryPort;
    private final MenuReportMapper mapper;

    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final CurrentUserService currentUserService;

    public EditMenuReportService(MenuReportRepositoryPort menuReportRepositoryPort,
                                 DishMenuRepositoryPort dishMenuRepositoryPort,
                                 ProductRepositoryPort productRepository,
                                 InventoryLotRepositoryPort inventoryLotRepository,
                                 UserRepositoryPort userRepositoryPort,
                                 MenuReportMapper mapper, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.dishMenuRepositoryPort = dishMenuRepositoryPort;
        this.productRepository = productRepository;
        this.inventoryLotRepository = inventoryLotRepository;
        this.userRepositoryPort = userRepositoryPort;
        this.mapper = mapper;
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.currentUserService = currentUserService;
    }


    @Override
    @Transactional
    public MenuReportResponseDTO editMenuReport(
            Integer id,
            EditMenuReportRequestDTO request
    ) {

        validarCocineras(request.getCooks());

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

        Integer actualQuantity = reporte.getQuantityPrepared();

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
        Integer userId = currentUserService.getCurrentUser().getId();

        revertirStock(reporte,userId);

        // =========================
        // APLICAR NUEVO CONSUMO
        // =========================
        StockConsumptionResultResponseDTO resultado =
                aplicarNuevoStock(
                        finalDishMenu,
                        newQty,userId
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

        registrarMovimiento(
                userId,
                null,
                finalDishMenu.getName(),
                BigDecimal.valueOf(request.getQuantityPrepared()),
                BigDecimal.valueOf(actualQuantity),
                PeruTime.now(),
                TransactionReferenceType.MENU,
                MovementType.MODIFICACION
        );

        return mapper.toDto(
                menuReportRepositoryPort.save(reporte)
        );
    }


    private void validarStockEdicion(
            MenuReport reporteActual,
            DishMenu nuevoMenu,
            BigDecimal nuevaCantidad
    ) {

        List<MissingProductResponseDTO> faltantes =
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
                        new MissingProductResponseDTO(
                                product.getId(),
                                product.getName(),
                                product.getUnit(),
                                requeridoNuevo.subtract(stockDisponible)
                        )
                );
            }
        }

        if (!faltantes.isEmpty()) {
            throw new InsufficientStockException(faltantes);
        }
    }
    private void revertirStock(MenuReport reporte,Integer userId) {

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
            BigDecimal actualStock = product.getStock();

            product.setStock(
                    product.getStock().add(total)
            );

            registrarMovimiento(
                    userId,
                    product.getId(),
                    product.getName(),
                    total,
                    actualStock,
                    PeruTime.now(),
                    TransactionReferenceType.INGREDIENTE,
                    MovementType.ENTRADA
                    );

            productRepository.updateStock(product);
        }
    }

    private StockConsumptionResultResponseDTO aplicarNuevoStock(
            DishMenu menu,
            BigDecimal qty,
            Integer userId
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
            BigDecimal actualStock = product.getStock();

            // Descontar del stock agregado del producto
            product.setStock(
                    product.getStock().subtract(requerido)
            );

            registrarMovimiento(
                    userId,
                    product.getId(),
                    product.getName(),
                    requerido,
                    actualStock,
                    PeruTime.now(),
                    TransactionReferenceType.INGREDIENTE,
                    MovementType.SALIDA
            );


            productRepository.updateStock(product);
        }

        return new StockConsumptionResultResponseDTO(
                movimientos,
                totalSpent
        );
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

    private void validarCocineras(List<Integer> cooks) {
        if (cooks == null || cooks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar al menos una cocinera"
            );
        }

        for (Integer cookId : cooks) {
            if (cookId == null) {
                throw new IllegalArgumentException(
                        "El ID de la cocinera no puede ser nulo"
                );
            }

            if (userRepositoryPort.findById(cookId).isEmpty()) {
                throw new UserNotFoundException("ID " + cookId);
            }
        }
    }
}
