package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificationsRequestDTO {
    private String editedClass;
    private String editedAttribute;
    private String previousValue;
    private String newValue;

}
