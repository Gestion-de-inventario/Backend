package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.Record;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuReportMapper {
    private final PersonMapper personMapper;

    private final ProductRecordMapper
            productRecordMapper;

    private final BeneficiaryControlMapper
            beneficiaryControlMapper;


    public MenuReportMapper(PersonMapper personMapper, ProductRecordMapper productRecordMapper, BeneficiaryControlMapper beneficiaryControlMapper) {
        this.personMapper = personMapper;
        this.productRecordMapper = productRecordMapper;
        this.beneficiaryControlMapper = beneficiaryControlMapper;
    }

    public MenuReport toDomain(ReporteMenuRequestDTO dto)
    {
        MenuReport menuReport = new MenuReport();
        menuReport.setMenu(dto.getMenu());
        menuReport.setCooks(dto.getCooks());
        return menuReport;
    }

    public ReporteMenuResponseDTO toDto(MenuReport menuReport)
    {
        ReporteMenuResponseDTO responseDTO = new ReporteMenuResponseDTO();
        responseDTO.setDate(menuReport.getDate());
        responseDTO.setId(menuReport.getId());
        responseDTO.setDay(menuReport.getDate().getDayOfWeek().toString());
        return responseDTO;
    }

    public List<ReporteMenuResponseDTO> toListDto(List<MenuReport> menuReports)
    {
        return menuReports.stream().map(this::toDto).toList();
    }

    public DetalleReporteMenuResponseDTO toDetalleDto(MenuReport reporte,
                                                      List<Person> cocineras,
                                                      List<Record> records,
                                                      List<BeneficiaryControl> beneficiarios,
                                                      ResumenReporteMenuResponseDTO resumen)
    {
        DetalleReporteMenuResponseDTO dto =
                new DetalleReporteMenuResponseDTO();

        dto.setId(reporte.getId());

        dto.setDate(reporte.getDate());

        dto.setDay(
                reporte.getDate()
                        .getDayOfWeek()
                        .toString()
        );

        dto.setCocineras(personMapper.toListPersonaResponseDTO(cocineras));

        dto.setRegistro(productRecordMapper.toListDto(records));

        dto.setBeneficiarios(beneficiaryControlMapper.toListDto(beneficiarios));

        dto.setResumenReporteMenu(resumen);
        return dto;
    }
}
