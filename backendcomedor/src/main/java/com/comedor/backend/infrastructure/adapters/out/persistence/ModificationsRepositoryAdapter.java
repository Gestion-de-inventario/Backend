package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.ModificationsEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.ModificationsEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.ModificationsJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ModificationsRepositoryAdapter implements ModificationsRepositoryPort {

    private final ModificationsJpaRepository modificationsJpaRepository;
    private final ModificationsEntityMapper modificationsEntityMapper;
    private final UserJpaRepository userJpaRepository;

    @Override
    public void registrar(Modifications modifications) {

        ModificationsEntity entity = new ModificationsEntity();

        UserEntity userEntity = userJpaRepository.findByUsername(modifications.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        entity.setUser(userEntity);
        entity.setEditedClass(modifications.getEditedClass());
        entity.setEditedAttribute(modifications.getEditedAttribute());
        entity.setPreviousValue(modifications.getPreviousValue());
        entity.setNewValue(modifications.getNewValue());
        entity.setDateTime(modifications.getDateTime());

        modificationsJpaRepository.save(entity);
    }

    @Override
    public List<Modifications> listar() {
        return modificationsJpaRepository.findAll()
                .stream()
                .map(modificationsEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
