package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;

public interface TagRepositoryPort {

    Tag createEtiqueta(Tag tag);
    Tag deactivateById(int id);
    Tag activateById(int id);
    List<Tag> getTags(Estado estado);
    boolean existByName(String name);
    Tag getTagById(int id);

}
