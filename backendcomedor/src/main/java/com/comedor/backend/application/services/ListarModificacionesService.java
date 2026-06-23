package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ModificationsMapper;
import com.comedor.backend.application.ports.in.ListarModificacionesUseCase;
import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public class ListarModificacionesService implements ListarModificacionesUseCase {

    private final ModificationsRepositoryPort modificationsRepositoryPort;
    private final ModificationsMapper modificationsMapper;

    public ListarModificacionesService(ModificationsRepositoryPort modificationsRepositoryPort, ModificationsMapper modificationsMapper) {
        this.modificationsRepositoryPort = modificationsRepositoryPort;
        this.modificationsMapper = modificationsMapper;
    }

    @Override
    public Page<ModificationsResponseDTO> list(int page, int size, LocalDate fechaInicio, LocalDate fechaFin) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (fechaInicio != null || fechaFin != null) {
            return modificationsRepositoryPort.listByPeriod(
                    fechaInicio != null ? fechaInicio.toString() : null,
                    fechaFin != null ? fechaFin.toString() : null,
                    pageable
            ).map(modificationsMapper::toResponseDTO);
        }
        return modificationsRepositoryPort.list(pageable).map(modificationsMapper::toResponseDTO);
    }
}
