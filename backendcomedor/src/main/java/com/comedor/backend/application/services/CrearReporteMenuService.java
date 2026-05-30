package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.CrearReporteMenuUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.application.ports.out.PurchaseDetailRepositoryPort;
import com.comedor.backend.domain.exceptions.ReporteMenuYaExistente;
// Faltará importar tus clases de Excepciones personalizadas y StockMovement
import com.comedor.backend.domain.model.*;
import com.comedor.backend.domain.model.enums.EstadoReporteMenu;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import org.springframework.transaction.annotation.Transactional; // MUY IMPORTANTE

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CrearReporteMenuService implements CrearReporteMenuUseCase {

    private final MenuReportRepositoryPort repository;
    private final DishMenuRepositoryPort dishMenuRepository;
    private final MenuReportMapper mapper;
    private final PurchaseDetailRepositoryPort purchaseDetailRepositoryPort;

    public CrearReporteMenuService(MenuReportRepositoryPort repository,
                                   DishMenuRepositoryPort dishMenuRepository,
                                   MenuReportMapper mapper,
                                   PurchaseDetailRepositoryPort purchaseDetailRepositoryPort) {
        this.repository = repository;
        this.dishMenuRepository = dishMenuRepository;
        this.mapper = mapper;
        this.purchaseDetailRepositoryPort = purchaseDetailRepositoryPort;
    }

    @Override
    @Transactional
    public ReporteMenuResponseDTO crearReporteMenu(ReporteMenuRequestDTO request) {
        if (repository.existByDate(LocalDate.now())) {
            throw new ReporteMenuYaExistente("Ya existe un reporte menu para hoy");
        }

        DishMenu dishMenu = dishMenuRepository.findById(request.getDishMenuId());
        List<StockMovement> movimientos = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (DishSupply supply : dishMenu.getSupplies()) {
            BigDecimal cantidadTotalNecesaria = supply.getQuantityNeeded()
                    .multiply(new BigDecimal(request.getQuantityPrepared()));

            List<PurchaseDetail> detallesDisponibles = purchaseDetailRepositoryPort.findAvailableByProduct(supply.getProduct().getId());

            for (PurchaseDetail detail : detallesDisponibles) {
                if (cantidadTotalNecesaria.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal aDescontar = cantidadTotalNecesaria.min(detail.getRemainingQuantity());

                StockMovement movement = new StockMovement();
                movement.setProduct(supply.getProduct());
                movement.setPurchaseDetail(detail);
                movement.setQuantityUsed(aDescontar);
                movement.setUnitCost(detail.getUnitPrice());
                movement.setTotalCost(aDescontar.multiply(detail.getUnitPrice()));
                movement.setMovementDate(LocalDateTime.now());
                movimientos.add(movement);

                totalSpent = totalSpent.add(movement.getTotalCost());
                detail.setRemainingQuantity(detail.getRemainingQuantity().subtract(aDescontar));
                purchaseDetailRepositoryPort.update(detail);

                cantidadTotalNecesaria = cantidadTotalNecesaria.subtract(aDescontar);
            }

            if (cantidadTotalNecesaria.compareTo(BigDecimal.ZERO) > 0) {
                String nombreProducto = (supply.getProduct().getName() != null)
                        ? supply.getProduct().getName()
                        : "ID Producto: " + supply.getProduct().getId();
                throw new RuntimeException("Stock insuficiente para: " + nombreProducto);
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
}