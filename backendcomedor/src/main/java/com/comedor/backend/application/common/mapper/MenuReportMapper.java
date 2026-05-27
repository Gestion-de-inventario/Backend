package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.StockMovement; // <-- Importación actualizada
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.DishMenu; // <-- Necesario para el toDomain
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.stereotype.Component;

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
        ReporteMenuResponseDTO responseDTO = new ReporteMenuResponseDTO();
        responseDTO.setDate(menuReport.getDate());
        responseDTO.setId(menuReport.getId());
        responseDTO.setDay(menuReport.getDate().getDayOfWeek().toString());
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

        // 1. Datos básicos
        dto.setId(reporte.getId());
        dto.setDate(reporte.getDate());
        dto.setDay(reporte.getDate().getDayOfWeek().toString());
        dto.setMenu(menu);

        // 🔥 AQUI ESTABA EL ERROR: Faltaban estas líneas
        dto.setQuantityPrepared(reporte.getQuantityPrepared());
        dto.setQuantityRemaining(reporte.getQuantityRemaining());
        dto.setStatus(reporte.getStatus() != null ? reporte.getStatus().name() : null);

        // 2. Listas
        dto.setCocineras(personMapper.toListPersonaResponseDTO(cocineras));
        dto.setRegistro(stockMovementMapper.toListDto(stockMovements));
        dto.setBeneficiarios(beneficiaryControlMapper.toListDto(beneficiarios));
        dto.setResumenReporteMenu(resumen);

        return dto;
    }
}