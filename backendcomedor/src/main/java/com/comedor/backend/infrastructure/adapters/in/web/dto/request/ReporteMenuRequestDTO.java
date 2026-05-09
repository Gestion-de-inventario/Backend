package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ReporteMenuRequestDTO {
    private String menu;
    private List<Integer> cooks;
}
