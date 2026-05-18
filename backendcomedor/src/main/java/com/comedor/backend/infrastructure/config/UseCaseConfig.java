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
            UserRepositoryPort usuarioRepository,
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
            UserRepositoryPort usuarioRepository,
            UserMapper userMapper

    ) {
        return new ListarUsariosActivosService(
                usuarioRepository,
                userMapper
        );
    }

    @Bean
    public ListarTodosLosUsuariosUseCase listarTodosLosUsuariosUseCase (UserRepositoryPort userRepositoryPort, UserMapper userMapper)
    {
        return new ListarTodoLosUsuariosService(
                userRepositoryPort,
                userMapper
        );
    }

    @Bean
    public CrearUsuarioUseCase crearUsuarioUseCase (UserRepositoryPort userRepositoryPort, UserMapper userMapper, RoleRepositoryPort roleRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder)
    {
        return new CrearUsuarioService(
                userRepositoryPort,
                userMapper,
                roleRepositoryPort,
                personRepositoryPort,
                passwordEncoder
        );
    }

    @Bean
    public EditarUsuarioService editarUsuarioService(UserMapper userMapper, UserRepositoryPort userRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new EditarUsuarioService(
                userMapper,
                userRepositoryPort,
                personRepositoryPort,
                passwordEncoder,
                registrarModificacionUseCase
        );
    }

    @Bean
    public DesactivarUsuarioService desactivarUsuarioService (UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new DesactivarUsuarioService(
                userRepositoryPort,
                userMapper,
                registrarModificacionUseCase
        );
    }

    @Bean
    public RegistrarBeneficiarioService beneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort) {
        return new RegistrarBeneficiarioService(beneficiaryRepositoryPort);
    }

    @Bean
    public ConsultarDatosPorDniService consultarDatosPorDniService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ReniecPort reniecPort) {
        return new ConsultarDatosPorDniService(beneficiaryRepositoryPort, reniecPort);
    }

    @Bean
    public ConsultarYRegistrarReniecService consultarYRegistrarReniecService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ConsultarDatosPorDniService consultarDatosPorDniUseCase) {
        return new ConsultarYRegistrarReniecService(beneficiaryRepositoryPort,consultarDatosPorDniUseCase);
    }

    @Bean
    public EditarBeneficiarioService editarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new EditarBeneficiarioService(beneficiaryRepositoryPort, registrarModificacionUseCase);
    }

    @Bean
    EditarProductoService editarProductoService(ProductRepositoryPort productRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase){
        return new EditarProductoService(productRepositoryPort, registrarModificacionUseCase);
    }

    @Bean
    ListarBeneficiarioServicePorEstado listarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryMapper beneficiaryMapper) {
        return new ListarBeneficiarioServicePorEstado(beneficiaryRepositoryPort, beneficiaryMapper);
    }

    @Bean
    public CrearCategoriaService crearCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        return new CrearCategoriaService(
                categoryRepositoryPort,
                categoryMapper
        );
    }

    @Bean
    public ListarCategoriasPorEstadoService listarCategoriasPorEstadoService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper)
    {
        return new ListarCategoriasPorEstadoService(
                categoryRepositoryPort,
                categoryMapper
        );
    }

    @Bean
    public CrearEtiquetaService crearEtiquetaService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper)
    {
        return new CrearEtiquetaService(tagRepositoryPort, tagMapper);
    }

    @Bean
    public ListarEtiquetasPorEstadoService listarEtiquetasPorEstadoService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper)
    {
        return new ListarEtiquetasPorEstadoService(tagRepositoryPort, tagMapper);
    }

    @Bean
    DesactivarCategoriaService desactivarCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new DesactivarCategoriaService(
                categoryRepositoryPort, categoryMapper, registrarModificacionUseCase
        );
    }
    @Bean
    DesactivarEtiquetaService desactivarEtiquetaService (TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new DesactivarEtiquetaService(
                tagRepositoryPort, tagMapper, registrarModificacionUseCase
        );
    }

    @Bean
    ActivarCategoriaService activarCategoriaService (CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new ActivarCategoriaService(
                categoryRepositoryPort, categoryMapper, registrarModificacionUseCase
        );
    }
    @Bean
    ActivarEtiquetaService activarEtiquetaService (TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new ActivarEtiquetaService(
                tagRepositoryPort, tagMapper, registrarModificacionUseCase
        );
    }

    @Bean
    ListarProductosPorEstadoService listarProductosPorEstadoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper)
    {
        return new ListarProductosPorEstadoService(productRepositoryPort, productMapper);
    }

    @Bean
    CrearProductoService crearProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort) {
        return new CrearProductoService(productRepositoryPort, productMapper, categoryRepositoryPort, tagRepositoryPort);

    }
    @Bean
    ActivarProductoService activarProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegistrarModificacionUseCase registrarModificacionUseCase){
        return new ActivarProductoService(productRepositoryPort, productMapper, registrarModificacionUseCase);
    }
    @Bean
    DesactivarProductoService desactivarProductoService (ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new DesactivarProductoService(productRepositoryPort, productMapper, registrarModificacionUseCase);
    }

    @Bean
    CrearReporteMenuService crearReporteMenuService (MenuReportRepositoryPort repository, MenuReportMapper mapper){
        return new CrearReporteMenuService(repository,mapper);
    }

    @Bean
    RegistrarTransaccionService registrarTransaccionService(TransactionRepositoryPort repository, TransactionMapper mapper)
    {
        return new RegistrarTransaccionService(repository,mapper);
    }

    @Bean
    ListarTransaccionesService listarTransaccionesService (TransactionRepositoryPort repository, TransactionMapper mapper)
    {
        return new ListarTransaccionesService(repository,mapper);
    }

    @Bean
    AgregarRegistroProductoService agregarRegistroProductoService (ProductRecordRepositoryPort productRecordRepositoryPort, ProductRecordMapper productRecordMapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService, ActualizarStockUseCase actualizarStockUseCase, RevisarStockUseCase revisarStockUseCase, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase)
    {
        return new AgregarRegistroProductoService(productRecordRepositoryPort, productRecordMapper,registrarTransaccionUseCase,currentUserService,actualizarStockUseCase,revisarStockUseCase, recalcularResumenReporteUseCase);
    }

    @Bean
    CurrentUserService currentUserService (UserRepositoryPort userRepositoryPort){
        return new CurrentUserService(userRepositoryPort);
    }

    @Bean
    RevisarStockService revisarStockService (ProductRepositoryPort productRepositoryPort)
    {
        return new RevisarStockService(productRepositoryPort);
    }

    @Bean
    ActualizarStockService actualizarStockService(ProductRepositoryPort productRepositoryPort)
    {
        return new ActualizarStockService(productRepositoryPort);
    }

    @Bean
    RecalcularResumenReporteService recalcularResumenReporteService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, ProductRecordRepositoryPort productRecordRepositoryPort)
    {
        return new RecalcularResumenReporteService(menuReportRepositoryPort, beneficiaryControlRepositoryPort, productRecordRepositoryPort);
    }

    @Bean
    EditarRegistroBeneficiarioService editarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase)
    {
        return new EditarRegistroBeneficiarioService(beneficiaryControlRepositoryPort, beneficiaryControlMapper,recalcularResumenReporteUseCase);
    }

    @Bean
    AgregarRegistroBeneficiarioService agregarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase)
    {
        return new AgregarRegistroBeneficiarioService(beneficiaryControlRepositoryPort, beneficiaryControlMapper,recalcularResumenReporteUseCase);
    }

    @Bean
    EliminarRegistroBeneficiarioService eliminarRegistroBeneficiarioService (BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase){
        return new EliminarRegistroBeneficiarioService(beneficiaryControlRepositoryPort,recalcularResumenReporteUseCase);
    }

    @Bean
    EditarRegistroProductoService editarRegistroProductoService (ProductRecordRepositoryPort productRecordRepositoryPort,
                                                                 ProductRecordMapper productRecordMapper,
                                                                 ActualizarStockUseCase actualizarStockUseCase,
                                                                 RegistrarTransaccionUseCase registrarTransaccionUseCase,
                                                                 RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,
                                                                 CurrentUserService currentUserService){
        return new EditarRegistroProductoService(productRecordRepositoryPort, productRecordMapper,actualizarStockUseCase,registrarTransaccionUseCase,recalcularResumenReporteUseCase,currentUserService);
    }

    @Bean
    EliminarRegistroProductoService eliminarRegistroProductoService (ProductRecordRepositoryPort productRecordRepositoryPort,
                                                                     RegistrarTransaccionUseCase registrarTransaccionUseCase,
                                                                     CurrentUserService currentUserService,
                                                                     RecalcularResumenReporteUseCase recalcularResumenReporteUseCase){
        return new EliminarRegistroProductoService(productRecordRepositoryPort,registrarTransaccionUseCase,currentUserService,recalcularResumenReporteUseCase);
    }

    @Bean
    ObtenerResumenReporteMenuService obtenerResumenReporteMenuService (MenuReportRepositoryPort menuReportRepositoryPort,
                                                                       BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
                                                                       SummaryMenuReportMapper summaryMenuReportMapper)
    {
        return new ObtenerResumenReporteMenuService(menuReportRepositoryPort, beneficiaryControlRepositoryPort, summaryMenuReportMapper);
    }

    @Bean
    ObtenerReporteMenuPorFechaService obtenerReporteMenuPorFechaService (MenuReportRepositoryPort menuReportRepositoryPort, MenuReportMapper menuReportMapper, PersonRepositoryPort personRepositoryPort, ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase)
    {
        return new ObtenerReporteMenuPorFechaService(menuReportRepositoryPort, menuReportMapper, personRepositoryPort,obtenerResumenReporteMenuUseCase);
    }

    @Bean
    ActivarUsuarioService activarUsuarioService (UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new ActivarUsuarioService(userRepositoryPort, userMapper, registrarModificacionUseCase);
    }

    @Bean
    CreateRoleService createRoleService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper){
        return new CreateRoleService(roleRepository,permissionRepository,roleDTOMapper);
    }

    @Bean
    EditRoleService editRoleService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper, RegistrarModificacionUseCase registrarModificacionUseCase){
        return new EditRoleService(roleRepository,permissionRepository,roleDTOMapper, registrarModificacionUseCase);
    }

    @Bean
    ListRolesByStatusService listRolesByStatusService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper){
        return new ListRolesByStatusService(roleRepository,roleDTOMapper);
    }

    @Bean
    ListRoleByIdService listRoleByIdService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper){
        return new ListRoleByIdService(roleRepository,roleDTOMapper);
    }

    @Bean
    ListAllPermissionsService listAllPermissionsService(PermissionRepositoryPort permissionRepository, PermissionMapper permissionMapper){
        return new ListAllPermissionsService(permissionRepository,permissionMapper);
    }

    @Bean
    RegistrarModificacionService registrarModificacionService(ModificationsRepositoryPort modificationsRepositoryPort, UserRepositoryPort userRepositoryPort) {
        return new RegistrarModificacionService(modificationsRepositoryPort, userRepositoryPort);
    }

    @Bean
    ListarModificacionesService listarModificacionesService(ModificationsRepositoryPort modificationsRepositoryPort, ModificationsMapper modificationsMapper) {
        return new ListarModificacionesService(modificationsRepositoryPort, modificationsMapper);
    }
}




