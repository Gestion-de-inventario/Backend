package com.comedor.backend.infrastructure.config;

import com.comedor.backend.application.common.mapper.*;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.application.services.*;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.comedor.backend.application.services.RegistrarBeneficiarioService;

import java.util.List;

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
    public EditarUsuarioService editarUsuarioService(UserMapper userMapper, UserRepositoryPort userRepositoryPort, PersonRepositoryPort personRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase,RoleRepositoryPort roleRepositoryPort)
    {
        return new EditarUsuarioService(
                userMapper,
                userRepositoryPort,
                personRepositoryPort,
                registrarModificacionUseCase,
                roleRepositoryPort
        );
    }

    @Bean
    public CambiarPasswordUseCase cambiarPasswordUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegistrarModificacionService registrarModificacionService) {
        return new CambiarPasswordService(
                userRepositoryPort,
                passwordEncoder,
                registrarModificacionService
        );
    }

    @Bean
    public ForceChangePasswordService changePasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegistrarModificacionService registrarModificacionService) {
        return new ForceChangePasswordService(
                userRepositoryPort,
                passwordEncoder,
                registrarModificacionService
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
    public RegistrarBeneficiarioService beneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort,BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort,BeneficiaryMapper mapper) {
        return new RegistrarBeneficiarioService(beneficiaryRepositoryPort,beneficiaryTypeRepositoryPort,mapper);
    }

    @Bean
    public ConsultarDatosPorDniService consultarDatosPorDniService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ReniecPort reniecPort) {
        return new ConsultarDatosPorDniService(beneficiaryRepositoryPort, reniecPort);
    }

    @Bean
    public ConsultarYRegistrarReniecService consultarYRegistrarReniecService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ConsultarDatosPorDniService consultarDatosPorDniUseCase,BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        return new ConsultarYRegistrarReniecService(beneficiaryRepositoryPort,consultarDatosPorDniUseCase,beneficiaryTypeRepositoryPort);
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
    CrearReporteMenuService crearReporteMenuService (MenuReportRepositoryPort repository,
                                                     DishMenuRepositoryPort dishMenuRepository, ProductRepositoryPort productRepository,
                                                     InventoryLotRepositoryPort inventoryLotRepository,
                                                     MenuReportMapper mapper
            , RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService){
        return new CrearReporteMenuService(repository,dishMenuRepository,productRepository,inventoryLotRepository,mapper,registrarTransaccionUseCase,currentUserService);
    }

    @Bean
    RegistrarTransaccionService registrarTransaccionService(TransactionRepositoryPort repository,ProductRepositoryPort productRepository ,TransactionMapper mapper)
    {
        return new RegistrarTransaccionService(repository,productRepository,mapper);
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
    EditarRegistroBeneficiarioService editarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,MenuReportRepositoryPort menuReportRepositoryPort)
    {
        return new EditarRegistroBeneficiarioService(beneficiaryControlRepositoryPort, beneficiaryControlMapper,recalcularResumenReporteUseCase,menuReportRepositoryPort);
    }

    @Bean
    AgregarRegistroBeneficiarioService agregarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,MenuReportRepositoryPort menuReportRepositoryPort)
    {
        return new AgregarRegistroBeneficiarioService(beneficiaryControlRepositoryPort, beneficiaryControlMapper,recalcularResumenReporteUseCase,menuReportRepositoryPort);
    }

    @Bean
    EliminarRegistroBeneficiarioService eliminarRegistroBeneficiarioService (BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, RecalcularResumenReporteUseCase recalcularResumenReporteUseCase,MenuReportRepositoryPort menuReportRepositoryPort){
        return new EliminarRegistroBeneficiarioService(beneficiaryControlRepositoryPort,recalcularResumenReporteUseCase,menuReportRepositoryPort);
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
    EditRoleService editRoleService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper, RegistrarModificacionUseCase registrarModificacionUseCase){
        return new EditRoleService(roleRepository,roleDTOMapper, registrarModificacionUseCase);
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

    @Bean
    ObtenerAlertasStockService obtenerAlertasStockService(ProductRepositoryPort productRepositoryPort) {
        return new ObtenerAlertasStockService(productRepositoryPort);
    }

    @Bean
    CreateRefreshTokenService createRefreshTokenService(RefreshTokenRepositoryPort repository){
        return new CreateRefreshTokenService(repository);
    }
    @Bean
    RefreshTokenService refreshTokenService(RefreshTokenRepositoryPort refreshTokenRepository, UserRepositoryPort userRepository, JwtUtil jwtUtil, AuthMapper authMapper){
        return new RefreshTokenService(refreshTokenRepository,userRepository,jwtUtil,authMapper);
    }

    @Bean
    LogoutService logoutService(RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new LogoutService(refreshTokenRepositoryPort);
    }

    @Bean
    ActivarBeneficiarioService activarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new ActivarBeneficiarioService(beneficiaryRepositoryPort, registrarModificacionUseCase);
    }

    @Bean
    DesactivarBeneficiarioService desactivarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new DesactivarBeneficiarioService(beneficiaryRepositoryPort, registrarModificacionUseCase);
    }

    @Bean
    AssignPermissionesService assignPermissionesService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper)
    {
        return new AssignPermissionesService(roleRepository,permissionRepository,roleDTOMapper);
    }
    @Bean
    RoleChangeStatusService roleChangeStatusService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        return new RoleChangeStatusService(roleRepository,roleDTOMapper,registrarModificacionUseCase);
    }
    @Bean
    CreatePurchaseService createPurchaseService (PurchaseRepositoryPort purchaseRepository,
                                                 ProductRepositoryPort productRepository,
                                                 PurchaseMapper purchaseMapper){
        return new CreatePurchaseService(purchaseRepository,productRepository,purchaseMapper);
    }

    @Bean
    ListDishMenusService listDishMenusService (DishMenuRepositoryPort repository,
                                               DishMenuMapper mapper)
    {
        return new ListDishMenusService(repository,mapper);
    }

    @Bean
    ListPurchaseService listPurchaseService(PurchaseRepositoryPort repository, PurchaseMapper mapper)
    {
        return new ListPurchaseService(repository,mapper);
    }
    @Bean
    ConfirmPurchaseUseCase confirmPurchaseUseCase(PurchaseRepositoryPort purchaseRepository, ProductRepositoryPort productRepository, PurchaseMapper mapper, RegistrarTransaccionUseCase registrarTransaccionUseCase, CurrentUserService currentUserService,InventoryLotRepositoryPort inventoryLotRepository)
    {
        return new ConfirmPurchaseService(purchaseRepository,productRepository,mapper,registrarTransaccionUseCase,currentUserService,inventoryLotRepository);
    }

    @Bean
    CreateDishMenuService createDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, DishMenuMapper dishMenuMapper){
        return new CreateDishMenuService(dishMenuRepositoryPort, productRepositoryPort,dishMenuMapper);
    }

    @Bean
    EditDishMenuService editDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase, DishMenuMapper dishMenuMapper){
        return new EditDishMenuService(dishMenuRepositoryPort, productRepositoryPort, registrarModificacionUseCase, dishMenuMapper);
    }

    @Bean
    ChangeStatusDishMenuService changeStatusDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase, DishMenuMapper dishMenuMapper){
        return new ChangeStatusDishMenuService(dishMenuRepositoryPort, registrarModificacionUseCase, dishMenuMapper);
    }

    @Bean
    ListBeneficiariesTypesByStatusUseCase listBeneficiariesTypesByStatusUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper)
    {
        return new ListBeneficiariesTypesByStatusService( repository, mapper);
    }

    @Bean
    ChangeStatusBeneficiaryTypeUseCase changeStatusBeneficiaryTypeUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryRepositoryPort beneficiaryRepository, BeneficiaryTypeMapper mapper, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new ChangeStatusBeneficiaryTypeService(repository,beneficiaryRepository, mapper, registrarModificacionUseCase);
    }

    @Bean
    CreateBeneficiaryTypeUseCase createBeneficiaryTypeUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper)
    {
        return new CreateBeneficiaryTypeService(repository, mapper);
    }

    @Bean
    EditBeneficiaryTypeUseCase editBeneficiaryTypeUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper, RegistrarModificacionUseCase registrarModificacionUseCase)
    {
        return new EditBeneficiaryTypeService(repository,mapper,registrarModificacionUseCase);
    }

}




