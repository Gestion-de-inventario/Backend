package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;

public interface CategoryRepositoryPort {

    Category createCategory(Category category);
    Category deactivateById(int id);
    Category activateById(int id);
    List<Category> getCategorys(Estado estado);
    boolean existByName(String name);
    Category getCategoryById(int id);
}
