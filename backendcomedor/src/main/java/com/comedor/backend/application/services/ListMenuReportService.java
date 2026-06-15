package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.ListMenuReportUseCase;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ReporteMenuResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public class ListMenuReportService implements ListMenuReportUseCase {
    private final MenuReportRepositoryPort repository;
    private final MenuReportMapper mapper;

    public ListMenuReportService(MenuReportRepositoryPort repository, MenuReportMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }



    @Override
    public Page<ReporteMenuResponseDTO> list(int page, int size, LocalDate startDate, LocalDate endDate) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("date"),
                        Sort.Order.desc("id")
                )
        );


        return repository.showMenuReport(
                startDate,
                endDate,
                pageable).map(mapper::toDto);
    }
}
