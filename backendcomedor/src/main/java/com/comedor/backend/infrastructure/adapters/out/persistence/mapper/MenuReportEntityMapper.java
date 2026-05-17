package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MenuReportEntityMapper {
    private final BeneficiaryControlEntityMapper beneficiaryControlEntityMapper;
    private final RecordEntityMapper recordEntityMapper;
    public MenuReportEntityMapper(BeneficiaryControlEntityMapper beneficiaryControlEntityMapper, RecordEntityMapper recordEntityMapper) {
        this.beneficiaryControlEntityMapper = beneficiaryControlEntityMapper;
        this.recordEntityMapper = recordEntityMapper;
    }

    public MenuReport toDomain(MenuReportEntity entity)
    {
        MenuReport menuReport = new MenuReport();
        menuReport.setId(entity.getId());
        menuReport.setDate(entity.getDate());
        menuReport.setCooks(entity.getCooks());
        menuReport.setMenu(entity.getMenu());
        menuReport.setProductRecord(recordEntityMapper.toListDomain(entity.getProductRecord()));
        menuReport.setBeneficiariosRecord(beneficiaryControlEntityMapper.toListDomain(entity.getBeneficiariosRecord()));
        menuReport.setTotalEarned(entity.getTotalEarned());
        menuReport.setTotalSpent(entity.getTotalSpent());
        return menuReport;
    }

    public MenuReportEntity toEntity(MenuReport menuReport)
    {
        MenuReportEntity menuReportEntity = new MenuReportEntity();
        menuReportEntity.setId(menuReport.getId());
        menuReportEntity.setDate(menuReport.getDate());
        menuReportEntity.setCooks(menuReport.getCooks());
        menuReportEntity.setMenu(menuReport.getMenu());
        menuReportEntity.setTotalEarned(menuReport.getTotalEarned());
        menuReportEntity.setTotalSpent(menuReport.getTotalSpent());
        return menuReportEntity;
    }

    public List<MenuReport> toListDomain(List<MenuReportEntity> entities)
    {
        return entities.stream().map(this::toDomain).toList();
    }
}
