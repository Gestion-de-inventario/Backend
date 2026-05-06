package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.common.mapper.EtiquetaMapper;
import com.comedor.backend.application.ports.out.EtiquetaRepositoryPort;
import com.comedor.backend.domain.exceptions.CategoriaNoEncontradaException;
import com.comedor.backend.domain.exceptions.EtiquetaNoEncontradaException;
import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.CategoriaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EtiquetaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.EtiquetaEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.EtiquetaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EtiquetaRepositoryAdapter implements EtiquetaRepositoryPort {

    private final EtiquetaJpaRepository etiquetaJpaRepository;
    private final EtiquetaEntityMapper etiquetaEntityMapper;
    @Override
    public Etiqueta createEtiqueta(Etiqueta etiqueta) {
        EtiquetaEntity etiquetaEntity = etiquetaEntityMapper.toEntity(etiqueta);
        return etiquetaEntityMapper.toDomain(etiquetaJpaRepository.save(etiquetaEntity));
    }

    @Override
    public Etiqueta deactivateById(int id) {
        EtiquetaEntity entity = etiquetaJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        entity.setStatus(Estado.INACTIVO);
        return etiquetaEntityMapper.toDomain(etiquetaJpaRepository.save(entity));
    }

    @Override
    public Etiqueta activateById(int id) {
        EtiquetaEntity entity = etiquetaJpaRepository.findById(id).orElseThrow(()-> new CategoriaNoEncontradaException("Categoria no encontrada"));
        entity.setStatus(Estado.ACTIVO);
        return etiquetaEntityMapper.toDomain(etiquetaJpaRepository.save(entity));
    }

    @Override
    public List<Etiqueta> getEtiquetas(Estado estado) {
        if (estado == null) {
            return etiquetaEntityMapper.toListDomain(
                    etiquetaJpaRepository.findAll()
            );
        } else if (estado == Estado.ACTIVO) {
            return etiquetaEntityMapper.toListDomain(
                    etiquetaJpaRepository.getAllEtiquetasActivas());
        }
        return etiquetaEntityMapper.toListDomain(
                etiquetaJpaRepository.getAllEtiquetasInactivas()
        );
    }
    @Override
    public boolean existByName(String name) {
        return etiquetaJpaRepository.existsByName(name);
    }

    @Override
    public Etiqueta getEtiquetaById(int id) {
        EtiquetaEntity entity = etiquetaJpaRepository.findById(id).orElseThrow(() -> new EtiquetaNoEncontradaException("Etiqueta no encontrada"));
        return etiquetaEntityMapper.toDomain(entity);
    }
}
