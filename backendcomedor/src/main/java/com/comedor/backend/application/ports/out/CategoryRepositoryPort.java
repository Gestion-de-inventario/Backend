package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.enums.Status;

import java.util.List;

public interface CategoryRepositoryPort {

    Category createCategory(Category category);
    Category deactivateById(int id);
    Category activateById(int id);
    List<Category> getCategorys(Status status);
    boolean existByName(String name);
    Category getCategoryById(int id);
}
