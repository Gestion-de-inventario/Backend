package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoriaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoriaEntityMapper {

    public Categoria toDomain(CategoriaEntity categoriaEntity)
    {
        if (categoriaEntity == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(categoriaEntity.getId());
        categoria.setName(categoriaEntity.getName());
        categoria.setStatus(categoriaEntity.getStatus());
        return categoria;
    }

    public CategoriaEntity toEntity(Categoria categoria)
    {
        if (categoria == null) return null;
        CategoriaEntity categoriaEntity = new CategoriaEntity();
        categoriaEntity.setName(categoria.getName());
        categoriaEntity.setStatus(categoria.getStatus());
        return categoriaEntity;
    }
    public List<Categoria> toListDomain(List<CategoriaEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
