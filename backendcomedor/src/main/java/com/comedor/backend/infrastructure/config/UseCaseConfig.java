package com.comedor.backend.infrastructure.config;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.CrearUsuarioUseCase;
import com.comedor.backend.application.ports.in.ListarUsuariosUseCase;
import com.comedor.backend.application.ports.in.LoginUseCase;
import com.comedor.backend.application.ports.out.RolRepositoryPort;
import com.comedor.backend.application.services.AuthService;
import com.comedor.backend.application.common.mapper.AuthMapper;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.application.services.CrearUsuarioService;
import com.comedor.backend.application.services.ListarUsarioService;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {





















    @Bean
    public LoginUseCase loginUseCase(
            UsuarioRepositoryPort usuarioRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            AuthMapper authMapper
    ) {
        return new AuthService(
                usuarioRepository,
                jwtUtil,
                passwordEncoder,
                authMapper
        );
    }

    @Bean
    public ListarUsuariosUseCase listarUsuariosUseCase(
            UsuarioRepositoryPort usuarioRepository,
            UsuarioMapper usuarioMapper

    ) {
        return new ListarUsarioService(
                usuarioRepository,
                usuarioMapper
        );
    }

    @Bean
    public CrearUsuarioUseCase crearUsuarioUseCase (UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper, RolRepositoryPort rolRepositoryPort,PasswordEncoder passwordEncoder)
    {
        return new CrearUsuarioService(
                usuarioRepositoryPort,
                usuarioMapper,
                rolRepositoryPort,
                passwordEncoder
        );
    }
}