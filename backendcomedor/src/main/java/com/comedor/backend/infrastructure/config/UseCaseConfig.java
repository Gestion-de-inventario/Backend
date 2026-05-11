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
    @Bean
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
    @Bean
    ActivarProductoService activarProductoService(ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper){
        return new ActivarProductoService(productoRepositoryPort,productoMapper);
    }
    @Bean
    DesactivarProductoService desactivarProductoService (ProductoRepositoryPort productoRepositoryPort, ProductoMapper productoMapper)
    {
        return new DesactivarProductoService(productoRepositoryPort,productoMapper);
    }

    @Bean
    CrearReporteMenuService crearReporteMenuService (ReporteMenuRepositoryPort repository, ReporteMenuMapper mapper){
        return new CrearReporteMenuService(repository,mapper);
    }

    @Bean
    RegistrarTransaccionService registrarTransaccionService(TransaccionRepositoryPort repository, TransaccionMapper mapper)
    {
        return new RegistrarTransaccionService(repository,mapper);
    }

    @Bean
    ListarTransaccionesService listarTransaccionesService (TransaccionRepositoryPort repository, TransaccionMapper mapper)
    {
        return new ListarTransaccionesService(repository,mapper);
    }

    @Bean
    AgregarRegistroProductoService agregarRegistroProductoService (RegistroProductoRepositoryPort registroProductoRepositoryPort, RegistroProductoMapper registroProductoMapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService, ActualizarStockUseCase actualizarStockUseCase, RevisarStockUseCase revisarStockUseCase,RecalcularResumenReporteUseCase recalcularResumenReporteUseCase)
    {
        return new AgregarRegistroProductoService(registroProductoRepositoryPort,registroProductoMapper,registrarTransaccionUseCase,currentUserService,actualizarStockUseCase,revisarStockUseCase, recalcularResumenReporteUseCase);
    }

    @Bean
    CurrentUserService currentUserService (UsuarioRepositoryPort usuarioRepositoryPort){
        return new CurrentUserService(usuarioRepositoryPort);
    }

    @Bean
    RevisarStockService revisarStockService (ProductoRepositoryPort productoRepositoryPort)
    {
        return new RevisarStockService(productoRepositoryPort);
    }

    @Bean
    ActualizarStockService actualizarStockService(ProductoRepositoryPort productoRepositoryPort)
    {
        return new ActualizarStockService(productoRepositoryPort);
    }

    @Bean
    RecalcularResumenReporteService recalcularResumenReporteService(ReporteMenuRepositoryPort reporteMenuRepositoryPort, ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort, RegistroProductoRepositoryPort registroProductoRepositoryPort)
    {
        return new RecalcularResumenReporteService(reporteMenuRepositoryPort,controlBeneficiarioRepositoryPort,registroProductoRepositoryPort);
    }

    @Bean
    EditarRegistroBeneficiarioService editarRegistroBeneficiarioService(ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort, ControlBeneficiarioMapper controlBeneficiarioMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase)
    {
        return new EditarRegistroBeneficiarioService(controlBeneficiarioRepositoryPort,controlBeneficiarioMapper,recalcularResumenReporteUseCase);
    }

    @Bean
    AgregarRegistroBeneficiarioService agregarRegistroBeneficiarioService(ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort, ControlBeneficiarioMapper controlBeneficiarioMapper,RecalcularResumenReporteUseCase recalcularResumenReporteUseCase)
    {
        return new AgregarRegistroBeneficiarioService(controlBeneficiarioRepositoryPort,controlBeneficiarioMapper,recalcularResumenReporteUseCase);
    }

    @Bean
    EliminarRegistroBeneficiarioService eliminarRegistroBeneficiarioService ( ControlBeneficiarioRepositoryPort controlBeneficiarioRepositoryPort, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase){
        return new EliminarRegistroBeneficiarioService(controlBeneficiarioRepositoryPort,recalcularResumenReporteUseCase);
    }

    @Bean
    EditarRegistroProductoService editarRegistroProductoService (RegistroProductoRepositoryPort registroProductoRepositoryPort,
                                                                 RegistroProductoMapper registroProductoMapper,
                                                                 ActualizarStockUseCase actualizarStockUseCase,
                                                                 RegistrarTransaccionUseCase registrarTransaccionUseCase,
                                                                 RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,
                                                                 CurrentUserService currentUserService){
        return new EditarRegistroProductoService(registroProductoRepositoryPort,registroProductoMapper,actualizarStockUseCase,registrarTransaccionUseCase,recalcularResumenReporteUseCase,currentUserService);
    }

    @Bean
    EliminarRegistroProductoService eliminarRegistroProductoService (RegistroProductoRepositoryPort registroProductoRepositoryPort,
                                                                     RegistrarTransaccionUseCase registrarTransaccionUseCase,
                                                                     CurrentUserService currentUserService,
                                                                     RecalcularResumenReporteUseCase recalcularResumenReporteUseCase){
        return new EliminarRegistroProductoService(registroProductoRepositoryPort,registrarTransaccionUseCase,currentUserService,recalcularResumenReporteUseCase);
    }

}




