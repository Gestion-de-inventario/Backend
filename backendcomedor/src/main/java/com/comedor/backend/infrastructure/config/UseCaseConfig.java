package com.comedor.backend.infrastructure.config;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.CrearUsuarioUseCase;
import com.comedor.backend.application.ports.in.ListarTodosLosUsuariosUseCase;
import com.comedor.backend.application.ports.in.ListarUsuariosActivosUseCase;
import com.comedor.backend.application.ports.in.LoginUseCase;
import com.comedor.backend.application.ports.out.PersonaRepositoryPort;
import com.comedor.backend.application.ports.out.RolRepositoryPort;
import com.comedor.backend.application.services.*;
import com.comedor.backend.application.common.mapper.AuthMapper;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.application.services.RegistrarBeneficiarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ListarUsuariosActivosUseCase listarUsuariosUseCase(
            UsuarioRepositoryPort usuarioRepository,
            UsuarioMapper usuarioMapper

    ) {
        return new ListarUsariosActivosService(
                usuarioRepository,
                usuarioMapper
        );
    }

    @Bean
    public ListarTodosLosUsuariosUseCase listarTodosLosUsuariosUseCase (UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper)
    {
        return new ListarTodoLosUsuariosService(
                usuarioRepositoryPort,
                usuarioMapper
        );
    }

    @Bean
    public CrearUsuarioUseCase crearUsuarioUseCase (UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper, RolRepositoryPort rolRepositoryPort, PersonaRepositoryPort personaRepositoryPort, PasswordEncoder passwordEncoder)
    {
        return new CrearUsuarioService(
                usuarioRepositoryPort,
                usuarioMapper,
                rolRepositoryPort,
                personaRepositoryPort,
                passwordEncoder
        );
    }

    @Bean
    public EditarUsuarioService editarUsuarioService(UsuarioMapper usuarioMapper,UsuarioRepositoryPort usuarioRepositoryPort, PersonaRepositoryPort personaRepositoryPort,PasswordEncoder passwordEncoder)
    {
        return new EditarUsuarioService(
                usuarioMapper,
                usuarioRepositoryPort,
                personaRepositoryPort,
                passwordEncoder
        );
    }

    @Bean
    public DesactivarUsuarioService desactivarUsuarioService (UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper)
    {
        return new DesactivarUsuarioService(
                usuarioRepositoryPort,
                usuarioMapper
        );
    }
    
    @Bean
    public RegistrarBeneficiarioService beneficiarioService(BeneficiarioRepositoryPort beneficiarioRepositoryPort) {
        return new RegistrarBeneficiarioService(beneficiarioRepositoryPort);
    }
}




