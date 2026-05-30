package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEntityMapper {
    private final RoleEntityMapper rolMapper;
    private final PersonEntityMapper personEntityMapper;

    public UserEntityMapper(RoleEntityMapper rolMapper, PersonEntityMapper personEntityMapper) {
        this.rolMapper = rolMapper;
        this.personEntityMapper = personEntityMapper;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                rolMapper.toDomain(entity.getRole()),
                entity.getStatus(),
                personEntityMapper.toDomain(entity.getPersona())
        );
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setRole(rolMapper.toEntity(user.getRol()));
        entity.setStatus(user.getStatus());
        entity.setPersona(personEntityMapper.toEntity(user.getPersona()));

        return entity;
    }

    public List<User> toListDomain(List<UserEntity> entities) {
       return entities.stream().map(this::toDomain).toList();
    }

}
