package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.CategoriaRepositoryPort;
import com.comedor.backend.domain.exceptions.EtiquetaNoEncontradaException;
import com.comedor.backend.domain.model.Categoria;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoriaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.CategoriaEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.CategoriaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository categoriaJpaRepository;
    private final CategoriaEntityMapper categoriaEntityMapper;

    @Override
    public Categoria createCategory(Categoria categoria) {
        CategoriaEntity entity = categoriaEntityMapper.toEntity(categoria);
        CategoriaEntity categoriaEntity = categoriaJpaRepository.save(entity);
        return (categoriaEntityMapper.toDomain(categoriaEntity));
    }

    @Override
    public Categoria deactivateById(int id) {

        CategoriaEntity entity = categoriaJpaRepository.findById(id).orElseThrow(()-> new EtiquetaNoEncontradaException("Etiqueta no encontrada"));
        entity.setStatus(Estado.INACTIVO);
        return categoriaEntityMapper.toDomain(categoriaJpaRepository.save(entity));
    }

    @Override
    public Categoria activateById(int id) {
        CategoriaEntity entity = categoriaJpaRepository.findById(id).orElseThrow(()-> new EtiquetaNoEncontradaException("Etiqueta no encontrada"));
        entity.setStatus(Estado.ACTIVO);
        return categoriaEntityMapper.toDomain(categoriaJpaRepository.save(entity));
    }

    @Override
    public List<Categoria> getCategorys(Estado estado) {
            if (estado == null) {
                return categoriaEntityMapper.toListDomain(
                        categoriaJpaRepository.findAll()
                );
            } else if (estado == Estado.ACTIVO) {
                return categoriaEntityMapper.toListDomain(
                        categoriaJpaRepository.getAllCategoriasActivas());
            }
                return categoriaEntityMapper.toListDomain(
                        categoriaJpaRepository.getAllCategoriasInactivas()
                );
        }

    @Override
    public boolean existByName(String name) {
        return categoriaJpaRepository.existsByName(name);
    }

}
