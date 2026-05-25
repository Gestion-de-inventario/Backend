package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Modifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModificationsRepositoryPort {
    void registrar(Modifications modifications);

    Page<Modifications> list(Pageable pageable);
}
