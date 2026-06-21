package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDonationRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;

public interface CreateDonationUseCase {
    DonationResponseDTO create(CreateDonationRequestDTO requestDTO);
}
