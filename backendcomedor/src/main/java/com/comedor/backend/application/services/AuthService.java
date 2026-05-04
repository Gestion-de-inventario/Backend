package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.AuthMapper;
import com.comedor.backend.application.ports.in.LoginUseCase;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.exceptions.CredencialesInvalidasException;
import com.comedor.backend.domain.exceptions.UsuarioDeshabilitadoException;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuthRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService implements LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    public AuthService(UsuarioRepositoryPort usuarioRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthMapper authMapper) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {

        Usuario user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CredencialesInvalidasException();
        }

        if(user.getStatus().equals(Estado.INACTIVO))
        {
            throw new UsuarioDeshabilitadoException("El usuario :"+user.getPersona().getName()+
                    " "+user.getPersona().getLastname()+" se encuentra deshabilitado, comunicarse con la administradora.");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return authMapper.toAuthResponseDTO(user, token);
    }
}
