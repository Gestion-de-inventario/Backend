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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    public ReporteMenuResponseDTO editMenuReport(Integer id ,EditMenuReportRequestDTO request) {

        MenuReport reporte =
                menuReportRepositoryPort.findById(id);

        // =========================
        // 1. VALIDAR BENEFICIARIOS
        // =========================
        if (!reporte.getBeneficiaryControls().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede editar menú con beneficiarios asociados"
            );
        }

        // =========================
        // 2. CARGAR NUEVO MENÚ
        // =========================
        DishMenu newDishMenu =
                dishMenuRepositoryPort.findById(request.getDishMenuId());

        BigDecimal oldQty =
                BigDecimal.valueOf(reporte.getQuantityPrepared());

        BigDecimal newQty =
                BigDecimal.valueOf(request.getQuantityPrepared());

        BigDecimal diff = newQty.subtract(oldQty);

        // =========================
        // 3. VALIDAR STOCK SI AUMENTA
        // =========================
        if (diff.compareTo(BigDecimal.ZERO) > 0) {

            validarStockDisponible(newDishMenu, diff);
        }

        // =========================
        // 4. REVERTIR STOCK VIEJO
        // =========================
        revertirStock(reporte);

        // =========================
        // 5. APLICAR NUEVO STOCK
        // =========================
        List<StockMovement> movimientos =
                aplicarNuevoStock(newDishMenu, newQty);

        // =========================
        // 6. ACTUALIZAR ENTIDAD
        // =========================
        List<Person> cooks =
                personRepositoryPort.findAllByIds(request.getCooks());

        reporte.setDishMenu(newDishMenu);
        reporte.setQuantityPrepared(request.getQuantityPrepared());
        reporte.setCooks(request.getCooks());
        reporte.setStockMovements(movimientos);

        // =========================
        // 7. LOG MODIFICACIÓN
        // =========================
        /*registrarMovimiento(
                currentUserService.getCurrentUser().getId(),
                reporte.getId(),
                BigDecimal.ZERO,
                TipoMovimiento.MODIFICACION,
                FuenteTransaccion.INVENTARIO
        );*/

        // =========================
        // 8. SAVE
        // =========================
        return mapper.toDto(
                menuReportRepositoryPort.save(reporte)
        );
    }
    private void validarStockDisponible(DishMenu menu, BigDecimal diff) {

        List<ProductoFaltanteResponseDTO> faltantes = new ArrayList<>();

        for (DishSupply supply : menu.getSupplies()) {

            BigDecimal requerido =
                    supply.getQuantityNeeded().multiply(diff);

            Product product = supply.getProduct();

            if (product.getStock().compareTo(requerido) < 0) {

                faltantes.add(new ProductoFaltanteResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getUnit(),
                        requerido.subtract(product.getStock())
                ));
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

    private List<StockMovement> aplicarNuevoStock(
            DishMenu menu,
            BigDecimal qty
    ) {

        List<StockMovement> movimientos = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (DishSupply supply : menu.getSupplies()) {

            BigDecimal pendiente =
                    supply.getQuantityNeeded().multiply(qty);

            List<InventoryLot> lotes =
                    inventoryLotRepository.findAvailableByProduct(
                            supply.getProduct().getId()
                    );

            for (InventoryLot lote : lotes) {

                if (pendiente.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal consumido =
                        pendiente.min(lote.getRemainingQuantity());

                StockMovement m = new StockMovement();
                m.setInventoryLot(lote);
                m.setQuantityUsed(consumido);
                m.setUnitCost(lote.getUnitCost());
                m.setTotalCost(consumido.multiply(lote.getUnitCost()));

                movimientos.add(m);

                lote.setRemainingQuantity(
                        lote.getRemainingQuantity().subtract(consumido)
                );

                inventoryLotRepository.update(lote);

                pendiente = pendiente.subtract(consumido);
            }
        }

        return movimientos;
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
