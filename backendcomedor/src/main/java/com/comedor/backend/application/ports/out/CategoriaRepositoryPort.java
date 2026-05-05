package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;

public interface CategoriaRepositoryPort {

    Categoria createCategory(Categoria categoria);
    Categoria deactivateById(int id);
    Categoria activateById(int id);
    List<Categoria> getCategorys(Estado estado);
    boolean existByName(String name);
    Categoria getCategoriaById(int id);
}
