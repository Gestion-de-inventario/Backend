package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.StockMovement;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.enums.MetodoPago;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ListMenuReportDetailResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MenuReportMapper {
    private final PersonMapper personMapper;

    // Reemplazamos ProductRecordMapper por el nuevo StockMovementMapper de aplicación
    private final StockMovementMapper stockMovementMapper;
    private final BeneficiaryControlMapper beneficiaryControlMapper;

    public MenuReportMapper(PersonMapper personMapper,
                            StockMovementMapper stockMovementMapper,
                            BeneficiaryControlMapper beneficiaryControlMapper) {
        this.personMapper = personMapper;
        this.stockMovementMapper = stockMovementMapper;
        this.beneficiaryControlMapper = beneficiaryControlMapper;
    }

    public MenuReport toDomain(ReporteMenuRequestDTO dto) {
        MenuReport menuReport = new MenuReport();

        // Seteamos la lista de IDs de las cocineras
        menuReport.setCooks(dto.getCooks());

        // Seteamos la cantidad preparada (Vital para el FIFO y el quantityRemaining)
        menuReport.setQuantityPrepared(dto.getQuantityPrepared());

        menuReport.setCooks(dto.getCooks());
        return menuReport;
    }

    public ReporteMenuResponseDTO toDto(MenuReport menuReport) {
        if (menuReport == null) return null;

        ReporteMenuResponseDTO responseDTO = new ReporteMenuResponseDTO();

        responseDTO.setId(menuReport.getId());
        responseDTO.setDate(menuReport.getDate());

        if (menuReport.getDate() != null) {
            responseDTO.setDay(menuReport.getDate().getDayOfWeek().toString());
        }

        // Agregamos el id del plato
        if (menuReport.getDishMenu() != null) {
            responseDTO.setDishId(menuReport.getDishMenu().getId());
        }

        // Agregamos el nombre del plato
        if (menuReport.getDishMenu() != null) {
            responseDTO.setDishName(menuReport.getDishMenu().getName());
        }

        // Agregamos las cantidades
        responseDTO.setQuantityPrepared(menuReport.getQuantityPrepared());
        responseDTO.setQuantityRemaining(menuReport.getQuantityRemaining());

        // Agregamos el estado
        if (menuReport.getStatus() != null) {
            responseDTO.setStatus(menuReport.getStatus());
        }

        // Agregamos el registro
        if(menuReport.getStockMovements() != null) {
            responseDTO.setRegisters(stockMovementMapper.toListDto(menuReport.getStockMovements()));
        }

        // Agregamos beneficiarios
        if(menuReport.getBeneficiaryControls() != null) {
            responseDTO.setBeneficiaries(beneficiaryControlMapper.toListDto(menuReport.getBeneficiaryControls()));
        }

        return responseDTO;
    }

    public List<ReporteMenuResponseDTO> toListDto(List<MenuReport> menuReports) {
        return menuReports.stream().map(this::toDto).toList();
    }

    public DetalleReporteMenuResponseDTO toDetalleDto(MenuReport reporte,
                                                      String menu,
                                                      List<Person> cocineras,
                                                      List<StockMovement> stockMovements,
                                                      List<BeneficiaryControl> beneficiarios,
                                                      ResumenReporteMenuResponseDTO resumen) {
        DetalleReporteMenuResponseDTO dto = new DetalleReporteMenuResponseDTO();

        dto.setId(reporte.getId());
        dto.setDate(reporte.getDate());
        dto.setDay(reporte.getDate().getDayOfWeek().toString());
        dto.setMenu(menu);


        dto.setQuantityPrepared(reporte.getQuantityPrepared());
        dto.setQuantityRemaining(reporte.getQuantityRemaining());
        dto.setStatus(reporte.getStatus() != null ? reporte.getStatus().name() : null);

        dto.setCocineras(personMapper.toListPersonaResponseDTO(cocineras));
        dto.setRegistro(stockMovementMapper.toListDto(stockMovements));
        dto.setBeneficiarios(beneficiaryControlMapper.toListDto(beneficiarios));
        dto.setResumenReporteMenu(resumen);

        return dto;
    }

    public ListMenuReportDetailResponseDTO
    listMenuReportDetailResponseDTO(
            List<DetalleReporteMenuResponseDTO> reports,
            BigDecimal totalEarned,
            BigDecimal totalSpent,
            BigDecimal net,
            int uniqueBeneficiaryCount,
            MetodoPago mostUsedPaymentMethod
    ) {

        ListMenuReportDetailResponseDTO dto =
                new ListMenuReportDetailResponseDTO();

        dto.setReports(reports);
        dto.setTotalEarned(totalEarned);
        dto.setTotalSpent(totalSpent);
        dto.setNet(net);
        dto.setUniqueBeneficiaryCount(uniqueBeneficiaryCount);
        dto.setMostUsedPaymentMethod(mostUsedPaymentMethod);

        return dto;
    }
}