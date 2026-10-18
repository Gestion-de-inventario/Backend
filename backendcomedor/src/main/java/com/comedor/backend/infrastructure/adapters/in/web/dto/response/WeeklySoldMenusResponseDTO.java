package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record WeeklySoldMenusResponseDTO(
        String dayName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        int menusSold
) {}