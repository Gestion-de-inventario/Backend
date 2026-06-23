package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.DishMenu;

import java.util.List;

public interface DishMenuRepositoryPort {
    List<DishMenu> findAll();
    DishMenu findById(Integer id);
    List<DishMenu> findAllActive();
    DishMenu save(DishMenu dishMenu);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
