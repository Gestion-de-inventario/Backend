package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.EmpresaConfigEntity;
import org.springframework.stereotype.Component;

@Component
public class EmpresaConfigEntityMapper {
    public EmpresaConfig toDomain(EmpresaConfigEntity entity) {
        EmpresaConfig domain = new EmpresaConfig();
        domain.setId(entity.getId());
        domain.setNombre(entity.getNombre());
        domain.setDescripcion(entity.getDescripcion());
        domain.setLogoBase64(entity.getLogoBase64());
        return domain;
    }

    public EmpresaConfigEntity toEntity(EmpresaConfig domain) {
        EmpresaConfigEntity entity = new EmpresaConfigEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setLogoBase64(domain.getLogoBase64());
        return entity;
    }
}
