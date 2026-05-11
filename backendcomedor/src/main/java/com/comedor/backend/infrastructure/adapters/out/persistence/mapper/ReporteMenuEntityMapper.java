package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ReporteMenuEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ReporteMenuEntityMapper {
    private final ControlBeneficiarioEntityMapper controlBeneficiarioEntityMapper;
    private final RegistroEntityMapper registroEntityMapper;
    public ReporteMenuEntityMapper(ControlBeneficiarioEntityMapper controlBeneficiarioEntityMapper, RegistroEntityMapper registroEntityMapper) {
        this.controlBeneficiarioEntityMapper = controlBeneficiarioEntityMapper;
        this.registroEntityMapper = registroEntityMapper;
    }

    public ReporteMenu toDomain(ReporteMenuEntity entity)
    {
        ReporteMenu reporteMenu = new ReporteMenu();
        reporteMenu.setId(entity.getId());
        reporteMenu.setDate(entity.getDate());
        reporteMenu.setCooks(entity.getCooks());
        reporteMenu.setMenu(entity.getMenu());
        reporteMenu.setProductRecord(registroEntityMapper.toListDomain(entity.getProductRecord()));
        reporteMenu.setBeneficiariosRecord(controlBeneficiarioEntityMapper.toListDomain(entity.getBeneficiariosRecord()));
        reporteMenu.setTotalEarned(entity.getTotalEarned());
        reporteMenu.setTotalSpent(entity.getTotalSpent());
        return reporteMenu;
    }

    public ReporteMenuEntity toEntity(ReporteMenu reporteMenu)
    {
        ReporteMenuEntity reporteMenuEntity = new ReporteMenuEntity();
        reporteMenuEntity.setId(reporteMenu.getId());
        reporteMenuEntity.setDate(reporteMenu.getDate());
        reporteMenuEntity.setCooks(reporteMenu.getCooks());
        reporteMenuEntity.setMenu(reporteMenu.getMenu());
        reporteMenuEntity.setTotalEarned(reporteMenu.getTotalEarned());
        reporteMenuEntity.setTotalSpent(reporteMenu.getTotalSpent());
        return reporteMenuEntity;
    }

    public List<ReporteMenu> toListDomain(List<ReporteMenuEntity> entities)
    {
        return entities.stream().map(this::toDomain).toList();
    }
}
