package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.CategoriaNoEncontradaException;
import com.comedor.backend.domain.exceptions.EtiquetaNoEncontradaException;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TagEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.TagEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepositoryPort {

    private final TagJpaRepository tagJpaRepository;
    private final TagEntityMapper tagEntityMapper;
    @Override
    public Tag createEtiqueta(Tag tag) {
        TagEntity tagEntity = tagEntityMapper.toEntity(tag);
        return tagEntityMapper.toDomain(tagJpaRepository.save(tagEntity));
    }

    @Override
    public Tag deactivateById(int id) {
        TagEntity entity = tagJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        entity.setStatus(Estado.INACTIVO);
        return tagEntityMapper.toDomain(tagJpaRepository.save(entity));
    }

    @Override
    public Tag activateById(int id) {
        TagEntity entity = tagJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        entity.setStatus(Estado.ACTIVO);
        return tagEntityMapper.toDomain(tagJpaRepository.save(entity));
    }

    @Override
    public List<Tag> getTags(Estado estado) {
        if (estado == null) {
            return tagEntityMapper.toListDomain(
                    tagJpaRepository.findAll()
            );
        } else if (estado == Estado.ACTIVO) {
            return tagEntityMapper.toListDomain(
                    tagJpaRepository.getAllEtiquetasActivas());
        }
        return tagEntityMapper.toListDomain(
                tagJpaRepository.getAllEtiquetasInactivas()
        );
    }
    @Override
    public boolean existByName(String name) {
        return tagJpaRepository.existsByName(name);
    }

    @Override
    public Tag getTagById(int id) {
        TagEntity entity = tagJpaRepository.findById(id).orElseThrow(() -> new EtiquetaNoEncontradaException("Etiqueta no encontrada"));
        return tagEntityMapper.toDomain(entity);
    }
}
