package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.LoginUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuthRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {
        return loginUseCase.login(request);
    }
}