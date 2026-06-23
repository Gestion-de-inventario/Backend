package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.GetMenuReportByIdUseCase;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.MenuReportResponseDTO;

public class GetMenuReporByIdService implements GetMenuReportByIdUseCase {
    private final MenuReportRepositoryPort repository;
    private final MenuReportMapper mapper;

    public GetMenuReporByIdService(MenuReportRepositoryPort repository, MenuReportMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MenuReportResponseDTO getMenuReportById(Integer id) {
        return mapper.toDto(repository.findById(id));
    }
}
