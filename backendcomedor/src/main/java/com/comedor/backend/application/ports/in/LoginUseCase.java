package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuthRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;

public interface LoginUseCase {

    AuthResponseDTO login(AuthRequestDTO request);

}