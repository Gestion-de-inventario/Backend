package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.DonationMapper;
import com.comedor.backend.application.ports.out.DonationRepositoryPort;
import com.comedor.backend.application.ports.out.ProductRepositoryPort;
import com.comedor.backend.domain.exceptions.DateException;
import com.comedor.backend.domain.model.Donation;
import com.comedor.backend.domain.model.DonationDetail;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.domain.model.enums.StatusOrder;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDonationDetailRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDonationRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import com.comedor.backend.infrastructure.config.PeruTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.17 Registrar orden de donación")
class CreateDonationOrderServiceTest {

    @Mock
    private DonationRepositoryPort donationRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    private DonationMapper donationMapper;

    private CreateDonationService service;

    @BeforeEach
    void setUp() {
        donationMapper = new DonationMapper();

        service = new CreateDonationService(
                donationRepository,
                donationMapper,
                productRepository
        );
    }

    @Test
    @DisplayName("Escenario 1: Crear orden de donación con un producto")
    void crearDonacion_conUnProducto_debeCrearOrdenPendienteConDetalle() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");

        CreateDonationRequestDTO request = crearRequest(
                fecha,
                List.of(crearDetalleRequest(1, new BigDecimal("10")))
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(donationRepository.save(any(Donation.class)))
                .thenAnswer(invocation -> {
                    Donation donation = invocation.getArgument(0);
                    donation.setId(100);

                    for (DonationDetail detail : donation.getDetails()) {
                        detail.setId(200);
                    }

                    return donation;
                });

        // when
        DonationResponseDTO response = service.create(request);

        // then
        assertEquals(100, response.getId());
        assertEquals(fecha, response.getDonationDate());
        assertEquals(StatusOrder.PENDIENTE.name(), response.getStatus());

        assertEquals(1, response.getDetails().size());
        assertEquals(100, response.getDetails().get(0).getDonationId());
        assertEquals(200, response.getDetails().get(0).getDonationDetailId());
        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals("ARROZ", response.getDetails().get(0).getProductName());
        assertEquals("KG", response.getDetails().get(0).getProductUnit());
        assertEquals(new BigDecimal("10"), response.getDetails().get(0).getQuantity());

        ArgumentCaptor<Donation> captor =
                ArgumentCaptor.forClass(Donation.class);

        verify(donationRepository).save(captor.capture());

        Donation donationCreada = captor.getValue();

        assertEquals(fecha, donationCreada.getDonationDate());
        assertEquals(StatusOrder.PENDIENTE, donationCreada.getStatus());
        assertEquals(1, donationCreada.getDetails().size());

        DonationDetail detail = donationCreada.getDetails().get(0);

        assertSame(arroz, detail.getProduct());
        assertEquals(new BigDecimal("10"), detail.getQuantity());
    }

    @Test
    @DisplayName("Escenario 2: Crear orden de donación con varios productos")
    void crearDonacion_conVariosProductos_debeCrearOrdenConTodosLosDetalles() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        Product aceite = crearProducto(2, "ACEITE", "L");
        Product fideo = crearProducto(3, "FIDEO", "KG");

        CreateDonationRequestDTO request = crearRequest(
                fecha,
                List.of(
                        crearDetalleRequest(1, new BigDecimal("10")),
                        crearDetalleRequest(2, new BigDecimal("5")),
                        crearDetalleRequest(3, new BigDecimal("8"))
                )
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(productRepository.getProductoById(2)).thenReturn(aceite);
        when(productRepository.getProductoById(3)).thenReturn(fideo);
        when(donationRepository.save(any(Donation.class)))
                .thenAnswer(invocation -> {
                    Donation donation = invocation.getArgument(0);
                    donation.setId(101);
                    return donation;
                });

        // when
        DonationResponseDTO response = service.create(request);

        // then
        assertEquals(101, response.getId());
        assertEquals(StatusOrder.PENDIENTE.name(), response.getStatus());
        assertEquals(3, response.getDetails().size());

        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals(new BigDecimal("10"), response.getDetails().get(0).getQuantity());

        assertEquals(2, response.getDetails().get(1).getProductId());
        assertEquals(new BigDecimal("5"), response.getDetails().get(1).getQuantity());

        assertEquals(3, response.getDetails().get(2).getProductId());
        assertEquals(new BigDecimal("8"), response.getDetails().get(2).getQuantity());

        ArgumentCaptor<Donation> captor =
                ArgumentCaptor.forClass(Donation.class);

        verify(donationRepository).save(captor.capture());

        Donation donationCreada = captor.getValue();

        assertEquals(3, donationCreada.getDetails().size());
    }

    @Test
    @DisplayName("Escenario 3: Crear orden desde productos faltantes precargados y ajustados")
    void crearDonacion_desdeProductosFaltantesPrecargados_debeUsarCantidadesDelRequestAjustado() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        Product lenteja = crearProducto(2, "LENTEJA", "KG");

        CreateDonationRequestDTO request = crearRequest(
                fecha,
                List.of(
                        crearDetalleRequest(1, new BigDecimal("15")),
                        crearDetalleRequest(2, new BigDecimal("6"))
                )
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(productRepository.getProductoById(2)).thenReturn(lenteja);
        when(donationRepository.save(any(Donation.class)))
                .thenAnswer(invocation -> {
                    Donation donation = invocation.getArgument(0);
                    donation.setId(102);
                    return donation;
                });

        // when
        DonationResponseDTO response = service.create(request);

        // then
        assertEquals(102, response.getId());
        assertEquals(2, response.getDetails().size());

        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals(new BigDecimal("15"), response.getDetails().get(0).getQuantity());

        assertEquals(2, response.getDetails().get(1).getProductId());
        assertEquals(new BigDecimal("6"), response.getDetails().get(1).getQuantity());
    }

    @Test
    @DisplayName("Escenario 4: Crear orden con lista editada")
    void crearDonacion_conListaEditada_debeGuardarSoloProductosEnviadosEnRequest() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");

        CreateDonationRequestDTO request = crearRequest(
                fecha,
                List.of(
                        crearDetalleRequest(1, new BigDecimal("12"))
                )
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(donationRepository.save(any(Donation.class)))
                .thenAnswer(invocation -> {
                    Donation donation = invocation.getArgument(0);
                    donation.setId(103);
                    return donation;
                });

        // when
        DonationResponseDTO response = service.create(request);

        // then
        assertEquals(1, response.getDetails().size());
        assertEquals(1, response.getDetails().get(0).getProductId());
        assertEquals(new BigDecimal("12"), response.getDetails().get(0).getQuantity());

        verify(productRepository).getProductoById(1);
        verify(productRepository, never()).getProductoById(2);

        ArgumentCaptor<Donation> captor =
                ArgumentCaptor.forClass(Donation.class);

        verify(donationRepository).save(captor.capture());

        Donation donationCreada = captor.getValue();

        assertEquals(1, donationCreada.getDetails().size());
    }

    @Test
    @DisplayName("Escenario 5: Rechazar orden con fecha anterior")
    void crearDonacion_conFechaAnterior_debeLanzarDateExceptionYNoGuardar() {
        // given
        CreateDonationRequestDTO request = crearRequest(
                PeruTime.today().minusDays(1),
                List.of(crearDetalleRequest(1, new BigDecimal("10")))
        );

        // when
        DateException exception = assertThrows(
                DateException.class,
                () -> service.create(request)
        );

        // then
        assertEquals(
                "Error al crear orden : La fecha de creación no puede ser menor a la actual ",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
        verifyNoInteractions(donationRepository);
    }

    @Test
    @DisplayName("Escenario 6: Crear orden pendiente sin modificar stock de productos")
    void crearDonacion_pendiente_noDebeModificarStockDeProductos() {
        // given
        LocalDate fecha = PeruTime.today();

        Product arroz = crearProducto(1, "ARROZ", "KG");
        arroz.setStock(new BigDecimal("100"));

        CreateDonationRequestDTO request = crearRequest(
                fecha,
                List.of(crearDetalleRequest(1, new BigDecimal("10")))
        );

        when(productRepository.getProductoById(1)).thenReturn(arroz);
        when(donationRepository.save(any(Donation.class)))
                .thenAnswer(invocation -> {
                    Donation donation = invocation.getArgument(0);
                    donation.setId(104);
                    return donation;
                });

        // when
        DonationResponseDTO response = service.create(request);

        // then
        assertEquals(StatusOrder.PENDIENTE.name(), response.getStatus());
        assertEquals(new BigDecimal("100"), arroz.getStock());

        verify(productRepository).getProductoById(1);
        verify(productRepository, never()).updateStock(any(Product.class));
        verify(productRepository, never()).updateProducto(any(Product.class));
    }

    private CreateDonationRequestDTO crearRequest(
            LocalDate date,
            List<CreateDonationDetailRequestDTO> details
    ) {
        CreateDonationRequestDTO request = new CreateDonationRequestDTO();
        request.setDate(date);
        request.setDetails(details);
        return request;
    }

    private CreateDonationDetailRequestDTO crearDetalleRequest(
            Integer productId,
            BigDecimal quantity
    ) {
        CreateDonationDetailRequestDTO detail = new CreateDonationDetailRequestDTO();
        detail.setProductId(productId);
        detail.setQuantity(quantity);
        return detail;
    }

    private Product crearProducto(
            Integer id,
            String name,
            String unit
    ) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setUnit(unit);
        product.setStatus(Status.ACTIVO);
        product.setStock(BigDecimal.ZERO);
        return product;
    }
}