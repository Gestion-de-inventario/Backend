package com.comedor.backend.infrastructure.config;

import com.comedor.backend.application.common.mapper.*;
import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.*;
import com.comedor.backend.application.services.*;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.comedor.backend.application.services.RegisterBeneficiaryService;

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
    public ListActiveUsersUseCase listarUsuariosUseCase(
            UserRepositoryPort usuarioRepository,
            UserMapper userMapper

    ) {
        return new ListActiveUsersService(
                usuarioRepository,
                userMapper
        );
    }

    @Bean
    public ListAllUsersUseCase listarTodosLosUsuariosUseCase (UserRepositoryPort userRepositoryPort, UserMapper userMapper)
    {
        return new ListAllUsersService(
                userRepositoryPort,
                userMapper
        );
    }

    @Bean
    public CreateUserUseCase crearUsuarioUseCase (UserRepositoryPort userRepositoryPort, UserMapper userMapper, RoleRepositoryPort roleRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder)
    {
        return new CreateUserService(
                userRepositoryPort,
                userMapper,
                roleRepositoryPort,
                personRepositoryPort,
                passwordEncoder
        );
    }

    @Bean
    public EditUserService editarUsuarioService(UserMapper userMapper, UserRepositoryPort userRepositoryPort, PersonRepositoryPort personRepositoryPort, RegisterModificationUseCase registerModificationUseCase, RoleRepositoryPort roleRepositoryPort)
    {
        return new EditUserService(
                userMapper,
                userRepositoryPort,
                personRepositoryPort,
                registerModificationUseCase,
                roleRepositoryPort
        );
    }

    @Bean
    public ChangePasswordUseCase cambiarPasswordUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegisterModificationService registrarModificacionService) {
        return new ChangePasswordService(
                userRepositoryPort,
                passwordEncoder,
                registrarModificacionService
        );
    }

    @Bean
    public ForceChangePasswordService changePasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegisterModificationService registrarModificacionService) {
        return new ForceChangePasswordService(
                userRepositoryPort,
                passwordEncoder,
                registrarModificacionService
        );
    }

    @Bean
    public DeactivateUserService desactivarUsuarioService (UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegisterModificationUseCase registerModificationUseCase)
    {
        return new DeactivateUserService(
                userRepositoryPort,
                userMapper,
                registerModificationUseCase
        );
    }

    @Bean
    public RegisterBeneficiaryService beneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort, BeneficiaryMapper mapper) {
        return new RegisterBeneficiaryService(beneficiaryRepositoryPort,beneficiaryTypeRepositoryPort,mapper);
    }

    @Bean
    public GetDataByDniService consultarDatosPorDniService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ReniecPort reniecPort) {
        return new GetDataByDniService(beneficiaryRepositoryPort, reniecPort);
    }

    @Bean
    public GetAndRegisterByReniecService consultarYRegistrarReniecService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, GetDataByDniService consultarDatosPorDniUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        return new GetAndRegisterByReniecService(beneficiaryRepositoryPort,consultarDatosPorDniUseCase,beneficiaryTypeRepositoryPort);
    }

    @Bean
    public EditBeneficiaryService editarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterModificationUseCase registerModificationUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        return new EditBeneficiaryService(beneficiaryRepositoryPort, registerModificationUseCase,beneficiaryTypeRepositoryPort);
    }

    @Bean
    EditProductService editarProductoService(ProductRepositoryPort productRepositoryPort, RegisterModificationUseCase registerModificationUseCase){
        return new EditProductService(productRepositoryPort, registerModificationUseCase);
    }

    @Bean
    ListBeneficiariesByStatusService listarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryMapper beneficiaryMapper) {
        return new ListBeneficiariesByStatusService(beneficiaryRepositoryPort, beneficiaryMapper);
    }

    @Bean
    public CreateCategoryService crearCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper) {
        return new CreateCategoryService(
                categoryRepositoryPort,
                categoryMapper
        );
    }

    @Bean
    public ListCategoriesByStatusService listarCategoriasPorEstadoService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper)
    {
        return new ListCategoriesByStatusService(
                categoryRepositoryPort,
                categoryMapper
        );
    }

    @Bean
    public CreateTagService crearEtiquetaService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper)
    {
        return new CreateTagService(tagRepositoryPort, tagMapper);
    }

    @Bean
    public ListTagsByStatusService listarEtiquetasPorEstadoService(TagRepositoryPort tagRepositoryPort, TagMapper tagMapper)
    {
        return new ListTagsByStatusService(tagRepositoryPort, tagMapper);
    }

    @Bean
    DeactivateCategoryService desactivarCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegisterModificationUseCase registerModificationUseCase) {
        return new DeactivateCategoryService(
                categoryRepositoryPort, categoryMapper, registerModificationUseCase
        );
    }
    @Bean
    DeactivateTagService desactivarEtiquetaService (TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegisterModificationUseCase registerModificationUseCase) {
        return new DeactivateTagService(
                tagRepositoryPort, tagMapper, registerModificationUseCase
        );
    }

    @Bean
    ActivateCategoryService activarCategoriaService (CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegisterModificationUseCase registerModificationUseCase) {
        return new ActivateCategoryService(
                categoryRepositoryPort, categoryMapper, registerModificationUseCase
        );
    }
    @Bean
    ActivateTagService activarEtiquetaService (TagRepositoryPort tagRepositoryPort, TagMapper tagMapper, RegisterModificationUseCase registerModificationUseCase)
    {
        return new ActivateTagService(
                tagRepositoryPort, tagMapper, registerModificationUseCase
        );
    }

    @Bean
    ListProductsByStatusService listarProductosPorEstadoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper)
    {
        return new ListProductsByStatusService(productRepositoryPort, productMapper);
    }

    @Bean
    CreateProductService crearProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, CategoryRepositoryPort categoryRepositoryPort, TagRepositoryPort tagRepositoryPort) {
        return new CreateProductService(productRepositoryPort, productMapper, categoryRepositoryPort, tagRepositoryPort);

    }
    @Bean
    ActivateProductService activarProductoService(ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegisterModificationUseCase registerModificationUseCase){
        return new ActivateProductService(productRepositoryPort, productMapper, registerModificationUseCase);
    }
    @Bean
    DeactivateProductService desactivarProductoService (ProductRepositoryPort productRepositoryPort, ProductMapper productMapper, RegisterModificationUseCase registerModificationUseCase)
    {
        return new DeactivateProductService(productRepositoryPort, productMapper, registerModificationUseCase);
    }

    @Bean
    CreateMenuReportService crearReporteMenuService (MenuReportRepositoryPort repository,
                                                     DishMenuRepositoryPort dishMenuRepository, ProductRepositoryPort productRepository,
                                                     InventoryLotRepositoryPort inventoryLotRepository,
                                                     MenuReportMapper mapper
            , RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService){
        return new CreateMenuReportService(repository,dishMenuRepository,productRepository,inventoryLotRepository,mapper, registerTransactionUseCase,currentUserService);
    }

    @Bean
    RegisterTransactionService registrarTransaccionService(TransactionRepositoryPort repository, ProductRepositoryPort productRepository , TransactionMapper mapper)
    {
        return new RegisterTransactionService(repository,productRepository,mapper);
    }

    @Bean
    ListTransactionsService listarTransaccionesService (TransactionRepositoryPort repository, TransactionMapper mapper)
    {
        return new ListTransactionsService(repository,mapper);
    }

    @Bean
    AddRecordProductService agregarRegistroProductoService (ProductRecordRepositoryPort productRecordRepositoryPort, ProductRecordMapper productRecordMapper, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService, UpdateStockUseCase updateStockUseCase, CheckStockUseCase checkStockUseCase, RecalculateSummaryReportUseCase recalculateSummaryReportUseCase)
    {
        return new AddRecordProductService(productRecordRepositoryPort, productRecordMapper, registerTransactionUseCase,currentUserService, updateStockUseCase, checkStockUseCase, recalculateSummaryReportUseCase);
    }

    @Bean
    CurrentUserService currentUserService (UserRepositoryPort userRepositoryPort){
        return new CurrentUserService(userRepositoryPort);
    }

    @Bean
    CheckStockService revisarStockService (ProductRepositoryPort productRepositoryPort)
    {
        return new CheckStockService(productRepositoryPort);
    }

    @Bean
    UpdateStockService actualizarStockService(ProductRepositoryPort productRepositoryPort)
    {
        return new UpdateStockService(productRepositoryPort);
    }

    @Bean
    RecalculateSummaryReportService recalcularResumenReporteService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, ProductRecordRepositoryPort productRecordRepositoryPort)
    {
        return new RecalculateSummaryReportService(menuReportRepositoryPort, beneficiaryControlRepositoryPort, productRecordRepositoryPort);
    }

    @Bean
    EditBeneficiaryRecordService editarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, BeneficiaryControlMapper beneficiaryControlMapper, RecalculateSummaryReportUseCase recalculateSummaryReportUseCase, MenuReportRepositoryPort menuReportRepositoryPort)
    {
        return new EditBeneficiaryRecordService(beneficiaryControlRepositoryPort, beneficiaryControlMapper, recalculateSummaryReportUseCase,menuReportRepositoryPort);
    }

    @Bean
    AddRecordBeneficiaryService agregarRegistroBeneficiarioService(BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
                                                                   BeneficiaryControlMapper beneficiaryControlMapper, BeneficiaryRepositoryPort beneficiaryRepositoryPort,
                                                                   RecalculateSummaryReportUseCase recalculateSummaryReportUseCase, MenuReportRepositoryPort menuReportRepositoryPort)
    {
        return new AddRecordBeneficiaryService(beneficiaryControlRepositoryPort, beneficiaryControlMapper,beneficiaryRepositoryPort, recalculateSummaryReportUseCase,menuReportRepositoryPort);
    }

    @Bean
    DeleteBeneficiaryRecordService eliminarRegistroBeneficiarioService (BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort, RecalculateSummaryReportUseCase recalculateSummaryReportUseCase, MenuReportRepositoryPort menuReportRepositoryPort){
        return new DeleteBeneficiaryRecordService(beneficiaryControlRepositoryPort, recalculateSummaryReportUseCase,menuReportRepositoryPort);
    }

    @Bean
    EditProductRegistryService editarRegistroProductoService (ProductRecordRepositoryPort productRecordRepositoryPort,
                                                              ProductRecordMapper productRecordMapper,
                                                              UpdateStockUseCase updateStockUseCase,
                                                              RegisterTransactionUseCase registerTransactionUseCase,
                                                              RecalculateSummaryReportUseCase recalculateSummaryReportUseCase,
                                                              CurrentUserService currentUserService){
        return new EditProductRegistryService(productRecordRepositoryPort, productRecordMapper, updateStockUseCase, registerTransactionUseCase, recalculateSummaryReportUseCase,currentUserService);
    }

    @Bean
    DeleteProductRecordService eliminarRegistroProductoService (ProductRecordRepositoryPort productRecordRepositoryPort,
                                                                RegisterTransactionUseCase registerTransactionUseCase,
                                                                CurrentUserService currentUserService,
                                                                RecalculateSummaryReportUseCase recalculateSummaryReportUseCase){
        return new DeleteProductRecordService(productRecordRepositoryPort, registerTransactionUseCase,currentUserService, recalculateSummaryReportUseCase);
    }

    @Bean
    GetSummaryMenuReportService obtenerResumenReporteMenuService (MenuReportRepositoryPort menuReportRepositoryPort,
                                                                  BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort,
                                                                  SummaryMenuReportMapper summaryMenuReportMapper)
    {
        return new GetSummaryMenuReportService(menuReportRepositoryPort, beneficiaryControlRepositoryPort, summaryMenuReportMapper);
    }

    @Bean
    ListMenuReportDetailService obtenerReporteMenuPorFechaService (MenuReportRepositoryPort menuReportRepositoryPort, MenuReportMapper menuReportMapper, PersonRepositoryPort personRepositoryPort, GetSummaryMenuReportUseCase getSummaryMenuReportUseCase)
    {
        return new ListMenuReportDetailService(menuReportRepositoryPort, menuReportMapper, personRepositoryPort, getSummaryMenuReportUseCase);
    }

    @Bean
    ActivateUserService activarUsuarioService (UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegisterModificationUseCase registerModificationUseCase)
    {
        return new ActivateUserService(userRepositoryPort, userMapper, registerModificationUseCase);
    }

    @Bean
    CreateRoleService createRoleService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper){
        return new CreateRoleService(roleRepository,permissionRepository,roleDTOMapper);
    }

    @Bean
    EditRoleService editRoleService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper, RegisterModificationUseCase registerModificationUseCase){
        return new EditRoleService(roleRepository,roleDTOMapper, registerModificationUseCase);
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
    RegisterModificationService registrarModificacionService(ModificationsRepositoryPort modificationsRepositoryPort, UserRepositoryPort userRepositoryPort) {
        return new RegisterModificationService(modificationsRepositoryPort, userRepositoryPort);
    }

    @Bean
    ListModificationsService listarModificacionesService(ModificationsRepositoryPort modificationsRepositoryPort, ModificationsMapper modificationsMapper) {
        return new ListModificationsService(modificationsRepositoryPort, modificationsMapper);
    }

    @Bean
    GetStockAlertsService obtenerAlertasStockService(ProductRepositoryPort productRepositoryPort) {
        return new GetStockAlertsService(productRepositoryPort);
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
    ActivateBeneficiaryService activarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterModificationUseCase registerModificationUseCase) {
        return new ActivateBeneficiaryService(beneficiaryRepositoryPort, registerModificationUseCase);
    }

    @Bean
    DeactivateBeneficiaryService desactivarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterModificationUseCase registerModificationUseCase) {
        return new DeactivateBeneficiaryService(beneficiaryRepositoryPort, registerModificationUseCase);
    }

    @Bean
    AssignPermissionesService assignPermissionesService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper)
    {
        return new AssignPermissionesService(roleRepository,permissionRepository,roleDTOMapper);
    }
    @Bean
    RoleChangeStatusService roleChangeStatusService(RoleRepositoryPort roleRepository, UserRepositoryPort userRepository,RoleMapper roleDTOMapper, RegisterModificationUseCase registerModificationUseCase) {
        return new RoleChangeStatusService(roleRepository,userRepository,roleDTOMapper, registerModificationUseCase);
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
    ConfirmPurchaseUseCase confirmPurchaseUseCase(PurchaseRepositoryPort purchaseRepository, ProductRepositoryPort productRepository, PurchaseMapper mapper, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService, InventoryLotRepositoryPort inventoryLotRepository)
    {
        return new ConfirmPurchaseService(purchaseRepository,productRepository,mapper, registerTransactionUseCase,currentUserService,inventoryLotRepository);
    }

    @Bean
    CreateDishMenuService createDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, DishMenuMapper dishMenuMapper){
        return new CreateDishMenuService(dishMenuRepositoryPort, productRepositoryPort,dishMenuMapper);
    }

    @Bean
    EditDishMenuService editDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepositoryPort, RegisterModificationUseCase registerModificationUseCase, DishMenuMapper dishMenuMapper){
        return new EditDishMenuService(dishMenuRepositoryPort, productRepositoryPort, registerModificationUseCase, dishMenuMapper);
    }

    @Bean
    ChangeStatusDishMenuService changeStatusDishMenuService(DishMenuRepositoryPort dishMenuRepositoryPort, RegisterModificationUseCase registerModificationUseCase, DishMenuMapper dishMenuMapper){
        return new ChangeStatusDishMenuService(dishMenuRepositoryPort, registerModificationUseCase, dishMenuMapper);
    }

    @Bean
    ListBeneficiariesTypesByStatusUseCase listBeneficiariesTypesByStatusUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper)
    {
        return new ListBeneficiariesTypesByStatusService( repository, mapper);
    }

    @Bean
    ChangeStatusBeneficiaryTypeUseCase changeStatusBeneficiaryTypeUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryRepositoryPort beneficiaryRepository, BeneficiaryTypeMapper mapper, RegisterModificationUseCase registerModificationUseCase)
    {
        return new ChangeStatusBeneficiaryTypeService(repository,beneficiaryRepository, mapper, registerModificationUseCase);
    }

    @Bean
    CreateBeneficiaryTypeUseCase createBeneficiaryTypeUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper)
    {
        return new CreateBeneficiaryTypeService(repository, mapper);
    }

    @Bean
    EditBeneficiaryTypeUseCase editBeneficiaryTypeUseCase(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper, RegisterModificationUseCase registerModificationUseCase)
    {
        return new EditBeneficiaryTypeService(repository,mapper, registerModificationUseCase);
    }

    @Bean
    ExportReportPDFService exportarReportePDFService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort){
        return new ExportReportPDFService(menuReportRepositoryPort, beneficiaryControlRepositoryPort);
    }

    @Bean
    ExportReportExcelService exportarReporteExcelService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort) {
        return new ExportReportExcelService(menuReportRepositoryPort, beneficiaryControlRepositoryPort);
    }

    @Bean
    GetDashboardService obtenerDashboardService(DashboardRepositoryPort dashboardRepositoryPort){
        return new GetDashboardService(dashboardRepositoryPort);
    }

    @Bean
    ExportTransactionsPDFService exportarTransaccionesPDFService(TransactionRepositoryPort repository, TransactionMapper mapper){
        return new ExportTransactionsPDFService(repository, mapper);
    }

    @Bean
    ExportModificationsPDFService exportarModificacionesPDFService(ModificationsRepositoryPort repository, ModificationsMapper mapper){
        return new ExportModificationsPDFService(repository, mapper);
    }

    @Bean
    ListMenuReportService listMenuReportService(MenuReportRepositoryPort repository, MenuReportMapper mapper)
        {
        return new ListMenuReportService(repository,mapper);
    }

    @Bean
    GetMenuReporByIdService getMenuReporByIdService(MenuReportRepositoryPort repository, MenuReportMapper mapper){
        return new GetMenuReporByIdService(repository,mapper);
    }
    @Bean
    EditMenuReportService editMenuReportService(MenuReportRepositoryPort menuReportRepositoryPort, DishMenuRepositoryPort dishMenuRepositoryPort, ProductRepositoryPort productRepository, InventoryLotRepositoryPort inventoryLotRepository, PersonRepositoryPort personRepositoryPort, MenuReportMapper mapper, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService){
        return new EditMenuReportService(menuReportRepositoryPort,dishMenuRepositoryPort,productRepository,inventoryLotRepository,personRepositoryPort,mapper, registerTransactionUseCase,currentUserService);
    }
    @Bean
    CreateDonationService createDonationService(DonationRepositoryPort repository, DonationMapper mapper, ProductRepositoryPort productRepository)
    {
        return new CreateDonationService(repository,mapper,productRepository);
    }

    @Bean
    ConfirmDonationService confirmDonationService (DonationRepositoryPort repository, DonationMapper mapper, ProductRepositoryPort productRepository, RegisterTransactionUseCase registerTransactionUseCase, CurrentUserService currentUserService, InventoryLotRepositoryPort inventoryLotRepository)
    {
        return new ConfirmDonationService(repository,mapper,productRepository, registerTransactionUseCase,currentUserService,inventoryLotRepository);
    }

    @Bean
    ListDonationService listDonationService (DonationRepositoryPort repository, DonationMapper mapper)
    {
        return new ListDonationService(repository,mapper);
    }

    @Bean
    ListOrderInsService listOrderInsService (OrderInRepositoryPort orderInRepositoryPort,
                                             OrderInMapper orderInMapper)
    {
        return new ListOrderInsService(orderInRepositoryPort,orderInMapper);
    }

    @Bean
    GetPurchaseByIdService getPurchaseByIdService(PurchaseRepositoryPort repository, PurchaseMapper mapper){
        return new GetPurchaseByIdService(repository,mapper);
    }

    @Bean
    GetDonationByIdService getDonationByIdService(DonationRepositoryPort repository, DonationMapper mapper){
        return new GetDonationByIdService(repository,mapper);
    }
}




