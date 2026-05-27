package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.DishMenu;

import java.util.List;

public interface DishMenuRepositoryPort {
    List<DishMenu> findAll();
}
