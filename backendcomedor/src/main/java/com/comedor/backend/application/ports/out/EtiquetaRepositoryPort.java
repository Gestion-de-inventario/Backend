package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Etiqueta;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;

public interface EtiquetaRepositoryPort {

    Etiqueta createEtiqueta(Etiqueta etiqueta);
    Etiqueta deactivateById(int id);
    Etiqueta activateById(int id);
    List<Etiqueta> getEtiquetas(Estado estado);
    boolean existByName(String name);
    Etiqueta getEtiquetaById(int id);

}
