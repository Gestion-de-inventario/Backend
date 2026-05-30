package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioBasicoDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PersonEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PersonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonRepositoryAdapter implements PersonRepositoryPort {

    private final PersonJpaRepository personJpaRepository;
    private final PersonEntityMapper personEntityMapper;
    @Override
    public List<Person> getAllPersonas() {
      return  personEntityMapper.toListDomain(personJpaRepository.getAllPersonas());
    }

    @Override
    public boolean existsByNameAndLastName(String name, String lastName) {
        return personJpaRepository.existsByNameAndLastName(name, lastName);
    }

    @Override
    public boolean existsByDni(String dni) {
        return personJpaRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByDniAndIdNot(String dni,int id) {
        return personJpaRepository.existsByDniAndIdNot(dni,id);
    }

    @Override
    public Optional<UsuarioBasicoDTO> findUsuarioBasicoDtoById(Integer id) {
        return Optional.empty();
    }

    @Override
    public boolean existsByNameAndLastNameAndIdNot(String name, String lastName, Integer user_id) {
        return personJpaRepository.existsByNameAndLastNameAndIdNot(name, lastName, user_id);
    }

    @Override
    public List<Person> findAllByIds(
            List<Integer> ids
    ) {

        List<PersonEntity> entities =
                personJpaRepository.findAllById(ids);

        return personEntityMapper.toListDomain(
                entities
        );
    }
}
