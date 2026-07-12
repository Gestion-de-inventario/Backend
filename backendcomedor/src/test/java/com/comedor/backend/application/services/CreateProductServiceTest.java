package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ProductMapper;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.application.ports.out.TagRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingProductException;
import com.comedor.backend.domain.exceptions.InvalidProductUnitException;
import com.comedor.backend.domain.model.Category;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Tag;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ProductRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ProductResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-2.1 Creación de Productos")
class CreateProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private TagRepositoryPort tagRepositoryPort;

    private ProductMapper productMapper;

    private CreateProductService service;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        service = new CreateProductService(
                productRepositoryPort,
                productMapper,
                categoryRepositoryPort,
                tagRepositoryPort
        );
    }

    @Test
    @DisplayName("Escenario 1: Crear producto con categoría y etiqueta")
    void crearProducto_conCategoriaYEtiqueta_debeCrearProductoConStockCeroCategoriaYEtiqueta() {
        // given
        ProductRequestDTO request = crearRequest(
                "Arroz",
                1,
                10,
                "KG",
                new BigDecimal("5")
        );

        Category categoria = crearCategoria(1, "Abarrotes");
        Tag etiqueta = crearTag(10, "Cereales");

        when(productRepositoryPort.existByName("ARROZ")).thenReturn(false);
        when(categoryRepositoryPort.getCategoryById(1)).thenReturn(categoria);
        when(tagRepositoryPort.getTagById(10)).thenReturn(etiqueta);
        when(productRepositoryPort.createProducto(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(100);
                    return product;
                });

        // when
        ProductResponseDTO response = service.crearProducto(request);

        // then
        assertEquals(100, response.getId());
        assertEquals("ARROZ", response.getName());
        assertEquals(Status.ACTIVO, response.getStatus());
        assertEquals(1, response.getCategoryId());
        assertEquals("ABARROTES", response.getCategoryName());
        assertEquals(10, response.getTagId());
        assertEquals("CEREALES", response.getTagName());
        assertEquals("KG", response.getUnit());
        assertEquals(BigDecimal.ZERO, response.getStock());
        assertEquals(new BigDecimal("5"), response.getReorderPoint());

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepositoryPort).createProducto(productCaptor.capture());

        Product productCreado = productCaptor.getValue();

        assertEquals("ARROZ", productCreado.getName());
        assertSame(categoria, productCreado.getCategory());
        assertSame(etiqueta, productCreado.getTag());
        assertEquals("KG", productCreado.getUnit());
        assertEquals(BigDecimal.ZERO, productCreado.getStock());
    }

    @Test
    @DisplayName("Escenario 2: Crear producto sin etiqueta")
    void crearProducto_sinEtiqueta_debeCrearProductoConCategoriaYEtiquetaNula() {
        // given
        ProductRequestDTO request = crearRequest(
                "Papa",
                2,
                null,
                "UNIDADES",
                new BigDecimal("10")
        );

        Category categoria = crearCategoria(2, "Verduras");

        when(productRepositoryPort.existByName("PAPA")).thenReturn(false);
        when(categoryRepositoryPort.getCategoryById(2)).thenReturn(categoria);
        when(productRepositoryPort.createProducto(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(101);
                    return product;
                });

        // when
        ProductResponseDTO response = service.crearProducto(request);

        // then
        assertEquals(101, response.getId());
        assertEquals("PAPA", response.getName());
        assertEquals(2, response.getCategoryId());
        assertEquals("VERDURAS", response.getCategoryName());
        assertEquals("UNIDADES", response.getUnit());
        assertEquals(BigDecimal.ZERO, response.getStock());

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepositoryPort).createProducto(productCaptor.capture());

        Product productCreado = productCaptor.getValue();

        assertEquals("PAPA", productCreado.getName());
        assertSame(categoria, productCreado.getCategory());
        assertNull(productCreado.getTag());

        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 3: Rechazar producto con nombre duplicado")
    void crearProducto_conNombreDuplicado_debeLanzarExistingProductExceptionYNoGuardar() {
        // given
        ProductRequestDTO request = crearRequest(
                "Arroz",
                1,
                null,
                "KG",
                new BigDecimal("5")
        );

        when(productRepositoryPort.existByName("ARROZ")).thenReturn(true);

        // when
        ExistingProductException exception = assertThrows(
                ExistingProductException.class,
                () -> service.crearProducto(request)
        );

        // then
        assertEquals(
                "Ya existe un producto con ese nombre :Arroz",
                exception.getMessage()
        );

        verify(productRepositoryPort).existByName("ARROZ");
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 4: Rechazar producto sin categoría")
    void crearProducto_sinCategoria_debeLanzarIllegalArgumentExceptionYNoGuardar() {
        // given
        ProductRequestDTO request = crearRequest(
                "Arroz",
                null,
                null,
                "KG",
                new BigDecimal("5")
        );

        when(productRepositoryPort.existByName("ARROZ")).thenReturn(false);

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.crearProducto(request)
        );

        // then
        assertEquals("La categoría es obligatoria", exception.getMessage());

        verify(productRepositoryPort).existByName("ARROZ");
        verify(productRepositoryPort, never()).createProducto(any(Product.class));
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 5: Crear producto convirtiendo nombre a mayúsculas")
    void crearProducto_conNombreMinusculaOMixto_debeGuardarNombreEnMayusculas() {
        // given
        ProductRequestDTO request = crearRequest(
                "aCeItE",
                1,
                null,
                "L",
                new BigDecimal("3")
        );

        Category categoria = crearCategoria(1, "Abarrotes");

        when(productRepositoryPort.existByName("ACEITE")).thenReturn(false);
        when(categoryRepositoryPort.getCategoryById(1)).thenReturn(categoria);
        when(productRepositoryPort.createProducto(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(102);
                    return product;
                });

        // when
        ProductResponseDTO response = service.crearProducto(request);

        // then
        assertEquals("ACEITE", response.getName());

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepositoryPort).createProducto(productCaptor.capture());

        Product productCreado = productCaptor.getValue();

        assertEquals("ACEITE", productCreado.getName());
        verify(productRepositoryPort).existByName("ACEITE");
    }

    @Test
    @DisplayName("Escenario 6: Mapear unidad estándar larga a abreviatura")
    void crearProducto_conUnidadKilogramos_debeGuardarUnidadNormalizadaComoKg() {
        // given
        ProductRequestDTO request = crearRequest(
                "Fideo",
                1,
                null,
                "Kilogramos",
                new BigDecimal("6")
        );

        Category categoria = crearCategoria(1, "Abarrotes");

        when(productRepositoryPort.existByName("FIDEO")).thenReturn(false);
        when(categoryRepositoryPort.getCategoryById(1)).thenReturn(categoria);
        when(productRepositoryPort.createProducto(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(103);
                    return product;
                });

        // when
        ProductResponseDTO response = service.crearProducto(request);

        // then
        assertEquals("KG", response.getUnit());

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepositoryPort).createProducto(productCaptor.capture());

        Product productCreado = productCaptor.getValue();

        assertEquals("KG", productCreado.getUnit());
    }

    @Test
    @DisplayName("Escenario 7: Rechazar unidad de medida no permitida")
    void crearProducto_conUnidadNoPermitida_debeLanzarInvalidProductUnitExceptionYNoGuardar() {
        // given
        ProductRequestDTO request = crearRequest(
                "Arroz",
                1,
                null,
                "CAJAS",
                new BigDecimal("5")
        );

        // when
        InvalidProductUnitException exception = assertThrows(
                InvalidProductUnitException.class,
                () -> service.crearProducto(request)
        );

        // then
        assertEquals("Unidad de medida no permitida", exception.getMessage());

        verifyNoInteractions(productRepositoryPort);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    @Test
    @DisplayName("Escenario 8: Rechazar producto sin unidad de medida")
    void crearProducto_sinUnidadMedida_debeLanzarIllegalArgumentExceptionYNoGuardar() {
        // given
        ProductRequestDTO request = crearRequest(
                "Arroz",
                1,
                null,
                null,
                new BigDecimal("5")
        );

        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.crearProducto(request)
        );

        // then
        assertEquals("La unidad de medida es obligatoria", exception.getMessage());

        verifyNoInteractions(productRepositoryPort);
        verifyNoInteractions(categoryRepositoryPort);
        verifyNoInteractions(tagRepositoryPort);
    }

    private ProductRequestDTO crearRequest(
            String name,
            Integer categoryId,
            Integer tagId,
            String unit,
            BigDecimal reorderPoint
    ) {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName(name);
        request.setCategoryId(categoryId);
        request.setTagId(tagId);
        request.setUnit(unit);
        request.setReorderPoint(reorderPoint);
        return request;
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