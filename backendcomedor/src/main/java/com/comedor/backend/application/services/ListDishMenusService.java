package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DishMenuMapper;
import com.comedor.backend.application.ports.in.ListDishMenusUseCase;
import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

import java.util.List;

public class ListDishMenusService
        implements ListDishMenusUseCase {

    private final DishMenuRepositoryPort repository;

    private final DishMenuMapper mapper;

    public ListDishMenusService(
            DishMenuRepositoryPort repository,
            DishMenuMapper mapper
    ) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<DishMenuResponseDTO> list() {

        return repository
                .findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
