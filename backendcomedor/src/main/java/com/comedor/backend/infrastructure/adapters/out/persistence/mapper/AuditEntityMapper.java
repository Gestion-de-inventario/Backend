package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Audit;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.AuditEntity;
import org.springframework.stereotype.Component;


@Component
public class AuditEntityMapper {

    public Audit toDomain(AuditEntity entity) {

        Audit audit = new Audit();

        audit.setId(entity.getId());
        audit.setEntityType(entity.getEntityType());
        audit.setEntityId(entity.getEntityId());
        audit.setEntityName(entity.getEntityName());
        audit.setAction(entity.getAction());
        audit.setDetails(entity.getDetails());
        audit.setDateTime(entity.getDateTime());

        User user = new User();
        user.setUsername(entity.getUser().getUsername());

        Person person = new Person();
        person.setName(entity.getUser().getPersona().getName());
        person.setLastname(entity.getUser().getPersona().getLastName());

        user.setPersona(person);

        audit.setUser(user);

        return audit;
    }

    public AuditEntity toEntity(Audit audit) {

        AuditEntity entity = new AuditEntity();

        entity.setId(audit.getId());
        entity.setEntityType(audit.getEntityType());
        entity.setEntityId(audit.getEntityId());
        entity.setEntityName(audit.getEntityName());
        entity.setAction(audit.getAction());
        entity.setDetails(audit.getDetails());
        entity.setDateTime(audit.getDateTime());

        return entity;
    }
}
