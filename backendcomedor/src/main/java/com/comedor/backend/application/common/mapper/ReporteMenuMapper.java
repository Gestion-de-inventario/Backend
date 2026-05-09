package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReporteMenuMapper {

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
}
