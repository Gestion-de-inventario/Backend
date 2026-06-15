package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EditMenuReportRequestDTO {
    private Integer dishMenuId;
    private Integer quantityPrepared;
    private List<Integer> cooks;
}
