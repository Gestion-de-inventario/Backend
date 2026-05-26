package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.common.mapper.AuthMapper;
import com.comedor.backend.application.ports.in.CreateRefreshTokenUseCase;
import com.comedor.backend.application.ports.in.LoginUseCase;
import com.comedor.backend.application.ports.in.LogoutUseCase;
import com.comedor.backend.application.ports.in.RefreshTokenUseCase;
import com.comedor.backend.application.ports.out.RefreshTokenRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuthRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final CreateRefreshTokenUseCase createRefreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UserRepositoryPort userRepository;
    private final AuthMapper authMapper;
    @Transactional
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO request,
            HttpServletResponse response
    ) {

        AuthResponseDTO authResponse = loginUseCase.login(request);

        String refreshToken = createRefreshTokenUseCase.create(authResponse.getId());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {

        AuthResponseDTO response = refreshTokenUseCase.refresh(refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        logoutUseCase.logout(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> me(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        AuthResponseDTO response =
                authMapper.toAuthResponseDTO(user, null);

        return ResponseEntity.ok(response);
    }
}