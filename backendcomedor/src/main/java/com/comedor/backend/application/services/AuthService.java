package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.AuthMapper;
import com.comedor.backend.application.ports.in.LoginUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.InvalidCredentialsException;
import com.comedor.backend.domain.exceptions.DisabledUserException;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuthRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService implements LoginUseCase {

    private final UserRepositoryPort usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    public AuthService(UserRepositoryPort usuarioRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthMapper authMapper) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {

        User user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if(user.getStatus().equals(Status.INACTIVO))
        {
            throw new DisabledUserException("El usuario :"+user.getPersona().getName()+
                    " "+user.getPersona().getLastname()+" se encuentra deshabilitado, comunicarse con la administradora.");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return authMapper.toAuthResponseDTO(user, token);
    }
}
