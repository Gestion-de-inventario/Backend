package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReporteMenuMapper {
    private final PersonaMapper personaMapper;

    private final RegistroProductoMapper
            registroProductoMapper;

    private final ControlBeneficiarioMapper
            controlBeneficiarioMapper;


    public ReporteMenuMapper(PersonaMapper personaMapper, RegistroProductoMapper registroProductoMapper, ControlBeneficiarioMapper controlBeneficiarioMapper) {
        this.personaMapper = personaMapper;
        this.registroProductoMapper = registroProductoMapper;
        this.controlBeneficiarioMapper = controlBeneficiarioMapper;
    }

    public ReporteMenu toDomain(ReporteMenuRequestDTO dto)
    {
        ReporteMenu reporteMenu = new ReporteMenu();
        reporteMenu.setMenu(dto.getMenu());
        reporteMenu.setCooks(dto.getCooks());
        return reporteMenu;
    }

    public ReporteMenuResponseDTO toDto(ReporteMenu reporteMenu)
    {
        ReporteMenuResponseDTO responseDTO = new ReporteMenuResponseDTO();
        responseDTO.setDate(reporteMenu.getDate());
        responseDTO.setId(reporteMenu.getId());
        responseDTO.setDay(reporteMenu.getDate().getDayOfWeek().toString());
        return responseDTO;
    }

    public List<ReporteMenuResponseDTO> toListDto(List<ReporteMenu> reporteMenus)
    {
        return reporteMenus.stream().map(this::toDto).toList();
    }

    public DetalleReporteMenuResponseDTO toDetalleDto(ReporteMenu reporte,
                                                      List<Persona> cocineras,
                                                      List<Registro> registros,
                                                      List<ControlBeneficiario> beneficiarios,
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

        dto.setCocineras(personaMapper.toListPersonaResponseDTO(cocineras));

        dto.setRegistro(registroProductoMapper.toListDto(registros));

        dto.setBeneficiarios(controlBeneficiarioMapper.toListDto(beneficiarios));

        dto.setResumenReporteMenu(resumen);
        return dto;
    }
}
