package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EtiquetaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class EtiquetaEntityMapper {
    public Etiqueta toDomain(EtiquetaEntity etiquetaEntity)
    {
        if (etiquetaEntity == null) return null;
        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setId(etiquetaEntity.getId());
        etiqueta.setName(etiquetaEntity.getName());
        etiqueta.setStatus(etiquetaEntity.getStatus());
        return etiqueta;
    }

    public EtiquetaEntity toEntity(Etiqueta etiqueta)
    {
        if (etiqueta == null) return null;
        EtiquetaEntity etiquetaEntity = new EtiquetaEntity();
        etiquetaEntity.setName(etiqueta.getName());
        etiquetaEntity.setStatus(etiqueta.getStatus());
        return etiquetaEntity;
    }
    public List<Etiqueta> toListDomain(List<EtiquetaEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
