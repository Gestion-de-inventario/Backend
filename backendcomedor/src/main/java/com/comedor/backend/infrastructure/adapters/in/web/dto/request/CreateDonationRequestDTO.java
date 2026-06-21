package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateDonationRequestDTO {
    private List<CreateDonationDetailRequestDTO> details;
}
