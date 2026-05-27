package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.DishMenuEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.DishMenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DishMenuRepositoryAdapter implements DishMenuRepositoryPort {

    private final DishMenuJpaRepository dishMenuJpaRepository;

    private final DishMenuEntityMapper mapper;

    @Override
    public List<DishMenu> findAll() {
        return dishMenuJpaRepository
                .findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public DishMenu findById(Integer id) {
        return dishMenuJpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " + id));
    }
}
