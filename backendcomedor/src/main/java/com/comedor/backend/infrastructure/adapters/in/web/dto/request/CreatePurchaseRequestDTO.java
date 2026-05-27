package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class CreatePurchaseRequestDTO {

    private List<CreatePurchaseDetailRequestDTO> details;

}