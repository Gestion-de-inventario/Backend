package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class DonationResponseDTO {
    private Integer id;

    private LocalDate donationDate;

    private String status;

    private List<DonationDetailResponseDTO> details;
}
