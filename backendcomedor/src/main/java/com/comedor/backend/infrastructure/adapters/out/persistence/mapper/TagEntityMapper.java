package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.TagEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class TagEntityMapper {
    public Tag toDomain(TagEntity tagEntity)
    {
        if (tagEntity == null) return null;
        Tag tag = new Tag();
        tag.setId(tagEntity.getId());
        tag.setName(tagEntity.getName());
        tag.setStatus(tagEntity.getStatus());
        return tag;
    }

    public TagEntity toEntity(Tag tag)
    {
        if (tag == null) return null;
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tag.getName());
        tagEntity.setStatus(tag.getStatus());
        return tagEntity;
    }
    public List<Tag> toListDomain(List<TagEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
