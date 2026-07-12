package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.InvalidProductUnitException;
import com.comedor.backend.domain.exceptions.ProductAlreadyExistsException;
import com.comedor.backend.domain.exceptions.ProductWithTransactionsException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditProductRequestDTO;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-2.2 Edición de Productos")
class EditProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private RegisterModificationUseCase registerModificationUseCase;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private TagRepositoryPort tagRepositoryPort;

    private ProductMapper productMapper;

    private EditProductService service;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        service = new EditProductService(
                productRepositoryPort,
                registerModificationUseCase,
                categoryRepositoryPort,
                tagRepositoryPort,
                productMapper
        );
    }

    @Test
    @DisplayName("Escenario 1: Editar producto sin transacciones vinculadas")
    void editarProducto_sinTransacciones_debeActualizarCamposRegistrarModificacionesYRetornarProductoEditado() {
        // given
        Integer productId = 1;

        Category categoriaActual = crearCategoria(1, "Abarrotes");
        Category categoriaNueva = crearCategoria(2, "Pastas");

        Tag etiquetaActual = crearTag(10, "Cereales");
        Tag etiquetaNueva = crearTag(20, "Fideos");

        Product product = crearProducto(
                productId,
                "ARROZ",
                categoriaActual,
                etiquetaActual,
                "KG",
                new BigDecimal("5.00")
        );

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setName("Fideo");
        request.setCategoryId(2);
        request.setTagId(20);
        request.setUnit("Litros");
        request.setReorderPoint(new BigDecimal("10.00"));

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(false);
        when(productRepositoryPort.existByNameAndIdNot("FIDEO", productId)).thenReturn(false);
        when(categoryRepositoryPort.getCategoryById(2)).thenReturn(categoriaNueva);
        when(tagRepositoryPort.getTagById(20)).thenReturn(etiquetaNueva);
        when(productRepositoryPort.updateProducto(product)).thenReturn(product);

        // when
        ProductResponseDTO response = service.editar(productId, request);

        // then
        assertEquals(productId, response.getId());
        assertEquals("FIDEO", response.getName());
        assertEquals(2, response.getCategoryId());
        assertEquals("PASTAS", response.getCategoryName());
        assertEquals(20, response.getTagId());
        assertEquals("FIDEOS", response.getTagName());
        assertEquals("L", response.getUnit());
        assertEquals(new BigDecimal("10.00"), response.getReorderPoint());

        assertEquals("FIDEO", product.getName());
        assertSame(categoriaNueva, product.getCategory());
        assertSame(etiquetaNueva, product.getTag());
        assertEquals("L", product.getUnit());
        assertEquals(new BigDecimal("10.00"), product.getReorderPoint());

        verify(productRepositoryPort).updateProducto(product);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase, times(5))
                .registrar(captor.capture());

        List<ModificationsRequestDTO> modificaciones = captor.getAllValues();

        assertEquals("name", modificaciones.get(0).getEditedAttribute());
        assertEquals("ARROZ", modificaciones.get(0).getPreviousValue());
        assertEquals("Fideo", modificaciones.get(0).getNewValue());

        assertEquals("category", modificaciones.get(1).getEditedAttribute());
        assertEquals("ABARROTES", modificaciones.get(1).getPreviousValue());
        assertEquals("PASTAS", modificaciones.get(1).getNewValue());

        assertEquals("tag", modificaciones.get(2).getEditedAttribute());
        assertEquals("CEREALES", modificaciones.get(2).getPreviousValue());
        assertEquals("FIDEOS", modificaciones.get(2).getNewValue());

        assertEquals("unit", modificaciones.get(3).getEditedAttribute());
        assertEquals("KG", modificaciones.get(3).getPreviousValue());
        assertEquals("L", modificaciones.get(3).getNewValue());

        assertEquals("reorderPoint", modificaciones.get(4).getEditedAttribute());
        assertEquals("5.00", modificaciones.get(4).getPreviousValue());
        assertEquals("10.00", modificaciones.get(4).getNewValue());
    }

    @Test
    @DisplayName("Escenario 2: Bloquear edición de nombre con transacciones vinculadas")
    void editarProducto_conTransaccionesYCambioNombre_debeLanzarProductWithTransactionsException() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setName("Fideo");

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(true);

        // when
        ProductWithTransactionsException exception = assertThrows(
                ProductWithTransactionsException.class,
                () -> service.editar(productId, request)
        );

        // then
        assertEquals(
                "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden",
                exception.getMessage()
        );

        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 3: Bloquear edición de categoría con transacciones vinculadas")
    void editarProducto_conTransaccionesYCambioCategoria_debeLanzarProductWithTransactionsException() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setCategoryId(2);

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(true);

        // when
        ProductWithTransactionsException exception = assertThrows(
                ProductWithTransactionsException.class,
                () -> service.editar(productId, request)
        );

        // then
        assertEquals(
                "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden",
                exception.getMessage()
        );

        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 4: Bloquear edición de etiqueta con transacciones vinculadas")
    void editarProducto_conTransaccionesYCambioEtiqueta_debeLanzarProductWithTransactionsException() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setTagId(20);

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(true);

        // when
        ProductWithTransactionsException exception = assertThrows(
                ProductWithTransactionsException.class,
                () -> service.editar(productId, request)
        );

        // then
        assertEquals(
                "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden",
                exception.getMessage()
        );

        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 5: Bloquear edición de unidad con transacciones vinculadas")
    void editarProducto_conTransaccionesYCambioUnidad_debeLanzarProductWithTransactionsException() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setUnit("Litros");

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(true);

        // when
        ProductWithTransactionsException exception = assertThrows(
                ProductWithTransactionsException.class,
                () -> service.editar(productId, request)
        );

        // then
        assertEquals(
                "El producto tiene transacciones vinculadas, solo se puede modificar el punto de reorden",
                exception.getMessage()
        );

        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 6: Permitir editar punto de reorden con transacciones vinculadas")
    void editarProducto_conTransaccionesYSoloReorderPoint_debeActualizarProductoYRegistrarModificacion() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setReorderPoint(new BigDecimal("15.00"));

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(true);
        when(productRepositoryPort.updateProducto(product)).thenReturn(product);

        // when
        ProductResponseDTO response = service.editar(productId, request);

        // then
        assertEquals(new BigDecimal("15.00"), product.getReorderPoint());
        assertEquals(new BigDecimal("15.00"), response.getReorderPoint());

        verify(productRepositoryPort).updateProducto(product);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("Producto", modification.getEditedClass());
        assertEquals("ARROZ", modification.getName());
        assertEquals("reorderPoint", modification.getEditedAttribute());
        assertEquals("5.00", modification.getPreviousValue());
        assertEquals("15.00", modification.getNewValue());

        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 7: Rechazar edición con nombre duplicado")
    void editarProducto_conNombreDuplicado_debeLanzarProductAlreadyExistsExceptionYNoActualizar() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setName("Fideo");

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(false);
        when(productRepositoryPort.existByNameAndIdNot("FIDEO", productId)).thenReturn(true);

        // when
        ProductAlreadyExistsException exception = assertThrows(
                ProductAlreadyExistsException.class,
                () -> service.editar(productId, request)
        );

        // then
        assertEquals(
                "Ya existe un producto con el nombre: Fideo",
                exception.getMessage()
        );

        verify(productRepositoryPort).existByNameAndIdNot("FIDEO", productId);
        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 8: Editar producto quitando etiqueta")
    void editarProducto_conTagIdCero_debeQuitarEtiquetaYRegistrarModificacion() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setTagId(0);

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(false);
        when(productRepositoryPort.updateProducto(product)).thenReturn(product);

        // when
        ProductResponseDTO response = service.editar(productId, request);

        // then
        assertNull(product.getTag());
        assertEquals(0, response.getTagId());

        verify(productRepositoryPort).updateProducto(product);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(tagRepositoryPort);

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("tag", modification.getEditedAttribute());
        assertEquals("CEREALES", modification.getPreviousValue());
        assertEquals("Sin etiqueta", modification.getNewValue());
    }

    @Test
    @DisplayName("Escenario 9: Editar unidad normalizando formato")
    void editarProducto_conUnidadLarga_debeNormalizarUnidadYRegistrarModificacion() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setUnit("Litros");

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(false);
        when(productRepositoryPort.updateProducto(product)).thenReturn(product);

        // when
        ProductResponseDTO response = service.editar(productId, request);

        // then
        assertEquals("L", product.getUnit());
        assertEquals("L", response.getUnit());

        verify(productRepositoryPort).updateProducto(product);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));

        ArgumentCaptor<ModificationsRequestDTO> captor =
                ArgumentCaptor.forClass(ModificationsRequestDTO.class);

        verify(registerModificationUseCase).registrar(captor.capture());

        ModificationsRequestDTO modification = captor.getValue();

        assertEquals("unit", modification.getEditedAttribute());
        assertEquals("KG", modification.getPreviousValue());
        assertEquals("L", modification.getNewValue());
    }

    @Test
    @DisplayName("Escenario 10: Rechazar unidad de medida no permitida")
    void editarProducto_conUnidadNoPermitida_debeLanzarInvalidProductUnitExceptionYNoActualizar() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setUnit("CAJAS");

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(false);

        // when
        InvalidProductUnitException exception = assertThrows(
                InvalidProductUnitException.class,
                () -> service.editar(productId, request)
        );

        // then
        assertEquals("Unidad de medida no permitida", exception.getMessage());

        verify(productRepositoryPort, never()).updateProducto(any(Product.class));
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(registerModificationUseCase);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 11: Evitar creación de registros duplicados al editar")
    void editarProducto_debeUsarUpdateProductoYNuncaCreateProducto() {
        // given
        Integer productId = 1;

        Product product = crearProductoBase(productId);

        EditProductRequestDTO request = new EditProductRequestDTO();
        request.setReorderPoint(new BigDecimal("8.00"));

        when(productRepositoryPort.getProductoById(productId)).thenReturn(product);
        when(productRepositoryPort.tieneTransaccionesVinculadas(productId)).thenReturn(false);
        when(productRepositoryPort.updateProducto(product)).thenReturn(product);

        // when
        ProductResponseDTO response = service.editar(productId, request);

        // then
        assertEquals(productId, response.getId());
        assertEquals(new BigDecimal("8.00"), response.getReorderPoint());

        verify(productRepositoryPort).updateProducto(product);
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
    }

    private Product crearProductoBase(Integer id) {
        Category categoria = crearCategoria(1, "Abarrotes");
        Tag tag = crearTag(10, "Cereales");

        return crearProducto(
                id,
                "ARROZ",
                categoria,
                tag,
                "KG",
                new BigDecimal("5.00")
        );
    }

    private Product crearProducto(
            Integer id,
            String name,
            Category category,
            Tag tag,
            String unit,
            BigDecimal reorderPoint
    ) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setTag(tag);
        product.setUnit(unit);
        product.setStock(new BigDecimal("100.00"));
        product.setReorderPoint(reorderPoint);
        product.setStatus(Status.ACTIVO);
        return product;
    }

    private Category crearCategoria(int id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setStatus(Status.ACTIVO);
        return category;
    }

    private Tag crearTag(int id, String name) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setStatus(Status.ACTIVO);
        return tag;
    }
}