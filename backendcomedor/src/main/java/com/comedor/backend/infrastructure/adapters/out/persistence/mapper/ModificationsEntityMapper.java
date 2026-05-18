package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ModificationsEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

@Component
public class ModificationsEntityMapper {

    public Modifications toDomain(ModificationsEntity entity) {
        Modifications mod = new Modifications();
        mod.setId(entity.getId());
        mod.setEditedClass(entity.getEditedClass());
        mod.setEditedAttribute(entity.getEditedAttribute());
        mod.setPreviousValue(entity.getPreviousValue());
        mod.setNewValue(entity.getNewValue());
        mod.setDateTime(entity.getDateTime());

        User user = new User();
        user.setUsername(entity.getUser().getUsername());

        Person person = new Person();
        person.setName(entity.getUser().getPersona().getName());
        person.setLastname(entity.getUser().getPersona().getLastName());
        user.setPersona(person);

        mod.setUser(user);

        return mod;
    }

}
