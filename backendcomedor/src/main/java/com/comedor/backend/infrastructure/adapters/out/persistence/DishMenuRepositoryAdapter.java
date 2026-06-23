package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.DishMenuRepositoryPort;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.DishMenuEntity;
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
        return dishMenuJpaRepository.findByIdWithSupplies(id) // Usamos el Join Fetch
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " + id));
    }

    @Override
    public List<DishMenu> findAllActive() {
        return dishMenuJpaRepository.findByStatus(Status.ACTIVO)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public DishMenu save(DishMenu dishMenu) {
        DishMenuEntity entity = mapper.toEntity(dishMenu);
        return mapper.toDomain(dishMenuJpaRepository.save(entity));
    }

    @Override
    public boolean existsByName(String name) {
        return dishMenuJpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Integer id) {
        return dishMenuJpaRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }


}
