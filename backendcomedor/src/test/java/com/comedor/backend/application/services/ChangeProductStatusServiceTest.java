package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-2.3 Eliminar Productos - Soft Delete")
class ChangeProductStatusServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private RegisterModificationUseCase registerModificationUseCase;

    private ProductMapper productMapper;

    private DeactivateProductService deactivateProductService;
    private ActivateProductService activateProductService;

    private ListProductsByStatusService listProductsByStatusService;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        deactivateProductService = new DeactivateProductService(
                productRepositoryPort,
                productMapper,
                registerModificationUseCase
        );

        activateProductService = new ActivateProductService(
                productRepositoryPort,
                productMapper,
                registerModificationUseCase
        );

        listProductsByStatusService = new ListProductsByStatusService(
                productRepositoryPort,
                productMapper
        );
    }

    @Test
    @DisplayName("Escenario 1: Desactivar producto correctamente")
    void desactivarProducto_existente_debeRetornarProductoInactivo() {
        // given
        int productId = 1;

        Product productoInactivo = crearProducto(productId, "ARROZ", Status.INACTIVO);

        when(productRepositoryPort.deactivateById(productId))
                .thenReturn(productoInactivo);

        // when
        ProductResponseDTO response =
                deactivateProductService.desactivarProductoPorId(productId);

        // then
        assertEquals(productId, response.getId());
        assertEquals("ARROZ", response.getName());
        assertEquals(Status.INACTIVO, response.getStatus());

        verify(productRepositoryPort).deactivateById(productId);
        verify(productRepositoryPort, never()).activateById(productId);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
    }

    @Test
    @DisplayName("Escenario 2: Registrar modificación al desactivar producto")
    void desactivarProducto_existente_debeRegistrarCambioDeEstadoActivoAInactivo() {
        // given
        int productId = 1;

        Product productoInactivo = crearProducto(productId, "ARROZ", Status.INACTIVO);

        when(productRepositoryPort.deactivateById(productId))
                .thenReturn(productoInactivo);

        // when
        deactivateProductService.desactivarProductoPorId(productId);

        // then
        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("Producto", modification.getEditedClass());
        assertEquals("ARROZ", modification.getName());
        assertEquals("status", modification.getEditedAttribute());
        assertEquals("ACTIVO", modification.getPreviousValue());
        assertEquals("INACTIVO", modification.getNewValue());
    }

    @Test
    @DisplayName("Escenario 3: Activar producto correctamente")
    void activarProducto_existente_debeRetornarProductoActivo() {
        // given
        int productId = 1;

        Product productoActivo = crearProducto(productId, "ARROZ", Status.ACTIVO);

        when(productRepositoryPort.activateById(productId))
                .thenReturn(productoActivo);

        // when
        ProductResponseDTO response =
                activateProductService.activarProductoPorId(productId);

        // then
        assertEquals(productId, response.getId());
        assertEquals("ARROZ", response.getName());
        assertEquals(Status.ACTIVO, response.getStatus());

        verify(productRepositoryPort).activateById(productId);
        verify(productRepositoryPort, never()).deactivateById(productId);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
    }

    @Test
    @DisplayName("Escenario 4: Registrar modificación al activar producto")
    void activarProducto_existente_debeRegistrarCambioDeEstadoInactivoAActivo() {
        // given
        int productId = 1;

        Product productoActivo = crearProducto(productId, "ARROZ", Status.ACTIVO);

        when(productRepositoryPort.activateById(productId))
                .thenReturn(productoActivo);

        // when
        activateProductService.activarProductoPorId(productId);

        // then
        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("Producto", modification.getEditedClass());
        assertEquals("ARROZ", modification.getName());
        assertEquals("status", modification.getEditedAttribute());
        assertEquals("INACTIVO", modification.getPreviousValue());
        assertEquals("ACTIVO", modification.getNewValue());
    }

    @Test
    @DisplayName("Escenario 5: Verificar soft delete usando desactivación lógica")
    void desactivarProducto_debeUsarDeactivateByIdYNoCrearProductoNuevo() {
        // given
        int productId = 1;

        Product productoInactivo = crearProducto(productId, "ARROZ", Status.INACTIVO);

        when(productRepositoryPort.deactivateById(productId))
                .thenReturn(productoInactivo);

        // when
        ProductResponseDTO response =
                deactivateProductService.desactivarProductoPorId(productId);

        // then
        assertEquals(productId, response.getId());
        assertEquals(Status.INACTIVO, response.getStatus());

        verify(productRepositoryPort).deactivateById(productId);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
    }

    private Product crearProducto(int id, String name, Status status) {
        Category category = new Category();
        category.setId(1);
        category.setName("Abarrotes");
        category.setStatus(Status.ACTIVO);

        Tag tag = new Tag();
        tag.setId(10);
        tag.setName("Cereales");
        tag.setStatus(Status.ACTIVO);

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setStatus(status);
        product.setCategory(category);
        product.setTag(tag);
        product.setUnit("KG");
        product.setStock(new BigDecimal("100.00"));
        product.setReorderPoint(new BigDecimal("5.00"));

        return product;
    }

    @Test
    @DisplayName("Escenario 6: Listar solo productos activos para selección")
    void listarProductosActivos_debeSolicitarSoloProductosConEstadoActivo() {
        // given
        Product arrozActivo = crearProducto(1, "ARROZ", Status.ACTIVO);
        Product aceiteActivo = crearProducto(2, "ACEITE", Status.ACTIVO);

        when(productRepositoryPort.getProductosByStatus(Status.ACTIVO))
                .thenReturn(List.of(arrozActivo, aceiteActivo));

        // when
        List<ProductResponseDTO> response =
                listProductsByStatusService.listarProductosPorEstado(Status.ACTIVO);

        // then
        assertEquals(2, response.size());

        assertEquals(1, response.get(0).getId());
        assertEquals("ARROZ", response.get(0).getName());
        assertEquals(Status.ACTIVO, response.get(0).getStatus());

        assertEquals(2, response.get(1).getId());
        assertEquals("ACEITE", response.get(1).getName());
        assertEquals(Status.ACTIVO, response.get(1).getStatus());

        verify(productRepositoryPort).getProductosByStatus(Status.ACTIVO);
        verify(productRepositoryPort, never()).getProductosByStatus(Status.INACTIVO);
    }
}