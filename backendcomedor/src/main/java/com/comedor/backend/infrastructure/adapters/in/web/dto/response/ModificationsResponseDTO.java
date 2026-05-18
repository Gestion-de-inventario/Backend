package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificationsResponseDTO {
    private int id;
    private String username;
    private String editedClass;
    private String editedAttribute;
    private String previousValue;
    private String newValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
}
