package com.comedor.backend.infrastructure.config;

import com.comedor.backend.application.common.mapper.*;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.application.services.*;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.comedor.backend.application.services.RegistrarBeneficiarioService;

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

    @Bean
    public ConsultarDatosPorDniService consultarDatosPorDniService(BeneficiarioRepositoryPort beneficiarioRepositoryPort, ReniecPort reniecPort) {
        return new ConsultarDatosPorDniService(beneficiarioRepositoryPort, reniecPort);
    }

    @Bean
    public ConsultarYRegistrarReniecService consultarYRegistrarReniecService(BeneficiarioRepositoryPort beneficiarioRepositoryPort, ConsultarDatosPorDniService consultarDatosPorDniUseCase) {
        return new ConsultarYRegistrarReniecService(beneficiarioRepositoryPort,consultarDatosPorDniUseCase);
    }

    @Bean
    public EditarBeneficiarioService editarBeneficiarioService(BeneficiarioRepositoryPort beneficiarioRepositoryPort) {
        return new EditarBeneficiarioService(beneficiarioRepositoryPort);
    }
  
    public CrearCategoriaService crearCategoriaService(CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper) {
        return new CrearCategoriaService(
                categoriaRepositoryPort,
                categoriaMapper
        );
    }

    @Bean
    public ListarCategoriasPorEstadoService listarCategoriasPorEstadoService(CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper)
    {
        return new ListarCategoriasPorEstadoService(
                categoriaRepositoryPort,
                categoriaMapper
        );
    }

    @Bean
    public CrearEtiquetaService crearEtiquetaService(EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper)
    {
        return new CrearEtiquetaService(etiquetaRepositoryPort,etiquetaMapper);
    }

    @Bean
    public ListarEtiquetasPorEstadoService listarEtiquetasPorEstadoService(EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper)
    {
        return new ListarEtiquetasPorEstadoService(etiquetaRepositoryPort,etiquetaMapper);
    }

    @Bean
    DesactivarCategoriaService desactivarCategoriaService(CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper) {
        return new DesactivarCategoriaService(
                categoriaRepositoryPort,categoriaMapper
        );
    }
    @Bean
    DesactivarEtiquetaService desactivarEtiquetaService (EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper) {
        return new DesactivarEtiquetaService(
                etiquetaRepositoryPort,etiquetaMapper
        );
    }

    @Bean
    ActivarCategoriaService activarCategoriaService (CategoriaRepositoryPort categoriaRepositoryPort, CategoriaMapper categoriaMapper) {
        return new ActivarCategoriaService(
                categoriaRepositoryPort,categoriaMapper
        );
    }
    @Bean
    ActivarEtiquetaService activarEtiquetaService (EtiquetaRepositoryPort etiquetaRepositoryPort, EtiquetaMapper etiquetaMapper)
    {
        return new ActivarEtiquetaService(
                etiquetaRepositoryPort,etiquetaMapper
        );
    }

    @Bean
    ListarProductosPorEstadoService listarProductosPorEstadoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper)
    {
        return new ListarProductosPorEstadoService(productoRepositoryPort,productoMapper);
    }

    @Bean
    CrearProductoService crearProductoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper,CategoriaRepositoryPort categoriaRepositoryPort,EtiquetaRepositoryPort etiquetaRepositoryPort) {
        return new CrearProductoService(productoRepositoryPort,productoMapper,categoriaRepositoryPort,etiquetaRepositoryPort);

    }
}




