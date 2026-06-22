package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class CreatePurchaseRequestDTO {
    //Refactor CUEVA
    private LocalDate date;
    private List<CreatePurchaseDetailRequestDTO> details;

}