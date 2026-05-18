package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Modifications;

import java.util.List;

public interface ModificationsRepositoryPort {
    void registrar(Modifications modifications);

    List<Modifications> listar();
}
