package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.exceptions.ReporteMenuNoEncontradoException;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.MenuReportEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.MenuReportEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.MenuReportJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.MenuReportSpecification;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.specification.PurchaseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuReportRepositoryAdapter implements MenuReportRepositoryPort {
    private final MenuReportJpaRepository menuReportJpaRepository;
    private final MenuReportEntityMapper menuReportEntityMapper;
    @Override
    public boolean existByDate(LocalDate date) {
        return menuReportJpaRepository.existsByDate(date);
    }

    @Override
    public MenuReport findByDate(LocalDate date) {
        return menuReportEntityMapper.toDomain(menuReportJpaRepository.findByDate(date));
    }

    @Override
    public MenuReport create(MenuReport menuReport) {

        MenuReportEntity entity =
                menuReportEntityMapper.toEntity(menuReport);

        return menuReportEntityMapper.toDomain(
                menuReportJpaRepository.save(entity)
        );
    }
    @Override
    public MenuReport save(MenuReport menuReport) {

        MenuReportEntity entity =
                menuReportEntityMapper.toEntity(menuReport);

        return menuReportEntityMapper.toDomain(
                menuReportJpaRepository.save(entity)
        );
    }

    @Override
    public MenuReport update(MenuReport menuReport) {
        MenuReportEntity entity =
                menuReportEntityMapper
                        .toEntity(menuReport);

        MenuReportEntity updated =
                menuReportJpaRepository
                        .save(entity);

        return menuReportEntityMapper
                .toDomain(updated);
    }

    private Specification<MenuReportEntity> buildSpecification(
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (
                startDate != null &&
                        endDate != null &&
                        startDate.isAfter(endDate)
        ) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser mayor que la fecha fin"
            );
        }

        Specification<MenuReportEntity> spec =
                (root, query, cb) -> cb.conjunction();

        if (startDate != null) {
            spec = spec.and(
                    MenuReportSpecification
                            .reportDateAfter(startDate)
            );
        }

        if (endDate != null) {
            spec = spec.and(
                    MenuReportSpecification
                            .reportDateBefore(endDate)
            );
        }

        return spec;
    }

    @Override
    public Page<MenuReport> showMenuReport(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        return menuReportJpaRepository
                .findAll(
                        buildSpecification(startDate, endDate),
                        pageable
                )
                .map(menuReportEntityMapper::toDomain);
    }

    @Override
    public List<MenuReport> showMenuReport(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return menuReportJpaRepository
                .findAll(
                        buildSpecification(startDate, endDate)
                )
                .stream()
                .map(menuReportEntityMapper::toDomain)
                .toList();
    }

    @Override
    public MenuReport findById(int id) {
        MenuReportEntity entity =
                menuReportJpaRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ReporteMenuNoEncontradoException(
                                        "No existe el reporte con id: " + id
                                )
                        );

        return menuReportEntityMapper
                .toDomain(entity);
    }
}
