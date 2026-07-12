package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ModificationsMapper;
import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.DishMenu;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.domain.model.enums.MovementType;
import com.comedor.backend.domain.model.enums.PaymentMethod;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.domain.model.enums.StatusMenuReport;
import com.comedor.backend.domain.model.enums.TransactionReferenceType;
import com.comedor.backend.domain.model.enums.TransactionSource;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-3.10 Exportación de Documentos")
class ExportDocumentsServiceTest {

    @Mock
    private MenuReportRepositoryPort menuReportRepositoryPort;

    @Mock
    private BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private ModificationsRepositoryPort modificationsRepositoryPort;

    @Mock
    private EmpresaConfigRepositoryPort empresaConfigRepositoryPort;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private ModificationsMapper modificationsMapper;

    private ExportReportExcelService exportReportExcelService;

    private ExportReportPDFService exportReportPDFService;

    private ExportTransactionsPDFService exportTransactionsPDFService;

    private ExportModificationsPDFService exportModificationsPDFService;

    @BeforeEach
    void setUp() {
        exportReportExcelService = new ExportReportExcelService(
                menuReportRepositoryPort,
                beneficiaryControlRepositoryPort
        );

        exportReportPDFService = new ExportReportPDFService(
                menuReportRepositoryPort,
                beneficiaryControlRepositoryPort,
                empresaConfigRepositoryPort
        );

        exportTransactionsPDFService = new ExportTransactionsPDFService(
                transactionRepositoryPort,
                transactionMapper,
                empresaConfigRepositoryPort
        );

        exportModificationsPDFService = new ExportModificationsPDFService(
                modificationsRepositoryPort,
                modificationsMapper,
                empresaConfigRepositoryPort
        );
    }

    @Test
    @DisplayName("Escenario 1: Exportar reporte de menú en Excel")
    void exportarReporteExcel_conReportes_debeGenerarWorkbookConHojasEsperadas() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        MenuReport reporte = crearMenuReportBase();

        when(menuReportRepositoryPort.showMenuReport(startDate, endDate))
                .thenReturn(List.of(reporte));

        when(beneficiaryControlRepositoryPort.findByReporteId(1))
                .thenReturn(List.of(crearBeneficiaryControl()));

        // when
        byte[] excel = exportReportExcelService.exportar(startDate, endDate);

        // then
        assertNotNull(excel);
        assertTrue(excel.length > 0);

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel))) {
            assertNotNull(workbook.getSheet("Resumen General"));
            assertNotNull(workbook.getSheet("Reportes"));
            assertNotNull(workbook.getSheet("Insumos"));
            assertNotNull(workbook.getSheet("Beneficiarios"));

            Sheet reportesSheet = workbook.getSheet("Reportes");

            assertEquals("ID Reporte", reportesSheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Fecha", reportesSheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("Plato", reportesSheet.getRow(0).getCell(2).getStringCellValue());

            assertEquals(1, (int) reportesSheet.getRow(1).getCell(0).getNumericCellValue());
            assertEquals("ARROZ CON POLLO", reportesSheet.getRow(1).getCell(2).getStringCellValue());
        }

        verify(menuReportRepositoryPort).showMenuReport(startDate, endDate);
        verify(beneficiaryControlRepositoryPort).findByReporteId(1);
    }

    @Test
    @DisplayName("Escenario 2: Calcular resumen general en Excel")
    void exportarReporteExcel_debeCalcularResumenGeneralCorrectamente() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        MenuReport reporte = crearMenuReportBase();

        when(menuReportRepositoryPort.showMenuReport(startDate, endDate))
                .thenReturn(List.of(reporte));

        when(beneficiaryControlRepositoryPort.findByReporteId(1))
                .thenReturn(List.of(crearBeneficiaryControl()));

        // when
        byte[] excel = exportReportExcelService.exportar(startDate, endDate);

        // then
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel))) {
            Sheet resumen = workbook.getSheet("Resumen General");

            assertEquals("REPORTE GENERAL DE MENÚS", resumen.getRow(0).getCell(0).getStringCellValue());

            assertEquals("Cantidad de reportes", resumen.getRow(4).getCell(0).getStringCellValue());
            assertEquals("1", resumen.getRow(4).getCell(1).getStringCellValue());

            assertEquals("Total platos preparados", resumen.getRow(5).getCell(0).getStringCellValue());
            assertEquals("10", resumen.getRow(5).getCell(1).getStringCellValue());

            assertEquals("Total platos entregados", resumen.getRow(6).getCell(0).getStringCellValue());
            assertEquals("7", resumen.getRow(6).getCell(1).getStringCellValue());

            assertEquals("Total platos sobrantes", resumen.getRow(7).getCell(0).getStringCellValue());
            assertEquals("3", resumen.getRow(7).getCell(1).getStringCellValue());

            assertEquals("Total recaudado", resumen.getRow(8).getCell(0).getStringCellValue());
            assertEquals("S/ 25.00", resumen.getRow(8).getCell(1).getStringCellValue());

            assertEquals("Total gastado", resumen.getRow(9).getCell(0).getStringCellValue());
            assertEquals("S/ 12.50", resumen.getRow(9).getCell(1).getStringCellValue());

            assertEquals("Balance", resumen.getRow(10).getCell(0).getStringCellValue());
            assertEquals("S/ 12.50", resumen.getRow(10).getCell(1).getStringCellValue());
        }
    }

    @Test
    @DisplayName("Escenario 3: Exportar reporte de menú en PDF")
    void exportarReportePDF_conReportes_debeGenerarPDFConFirmaYSello() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        MenuReport reporte = crearMenuReportBase();

        when(menuReportRepositoryPort.showMenuReport(startDate, endDate))
                .thenReturn(List.of(reporte));

        when(beneficiaryControlRepositoryPort.findByReporteId(1))
                .thenReturn(List.of(crearBeneficiaryControl()));

        // when
        byte[] pdf = exportReportPDFService.exportar(startDate, endDate);

        // then
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);

        String text = extraerTextoPDF(pdf);

        assertTrue(text.contains("REPORTE GENERAL DE MENÚS"));
        assertTrue(text.contains("ARROZ CON POLLO"));
        assertTrue(text.contains("INSUMOS UTILIZADOS"));
        assertTrue(text.contains("CONTROL DE BENEFICIARIOS"));
        assertTrue(text.contains("Firma Responsable"));
        assertTrue(text.contains("Sello del Comedor"));

        verify(menuReportRepositoryPort).showMenuReport(startDate, endDate);
        verify(beneficiaryControlRepositoryPort).findByReporteId(1);
    }

    @Test
    @DisplayName("Escenario 4: Exportar transacciones en PDF")
    void exportarTransaccionesPDF_conPeriodo_debeConsultarFiltrosYGenerarPDFValido() throws Exception {
        // given
        LocalDate fechaInicio = LocalDate.of(2026, 7, 1);
        LocalDate fechaFin = LocalDate.of(2026, 7, 31);

        Transactions transaccion = mock(Transactions.class);

        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(1);
        dto.setDateTime(LocalDateTime.of(2026, 7, 10, 10, 30));
        dto.setItemName("ARROZ");
        dto.setReferenceType(TransactionReferenceType.INGREDIENTE);
        dto.setType(MovementType.ENTRADA);
        dto.setSource(TransactionSource.COMPRA);
        dto.setAmount(new BigDecimal("10.00"));
        dto.setFinalStock(new BigDecimal("20.00"));
        dto.setPersonaName("JUAN");
        dto.setPersonaLastName("PEREZ");

        when(transactionRepositoryPort.showTransaccionesByPeriod("2026-07-01", "2026-07-31"))
                .thenReturn(List.of(transaccion));

        when(empresaConfigRepositoryPort.obtener())
                .thenReturn(crearEmpresaConfig());

        when(transactionMapper.toDTO(transaccion))
                .thenReturn(dto);

        // when
        byte[] pdf = exportTransactionsPDFService.exportar(fechaInicio, fechaFin);

        // then
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);

        String text = extraerTextoPDF(pdf);

        assertTrue(text.contains("Comedor Municipal"));
        assertTrue(text.contains("ARROZ"));
        assertTrue(text.contains("ENTRADA"));
        assertTrue(text.contains("COMPRA"));
        assertTrue(text.contains("Total de registros: 1"));

        verify(transactionRepositoryPort)
                .showTransaccionesByPeriod("2026-07-01", "2026-07-31");

        verify(transactionMapper).toDTO(transaccion);
    }

    @Test
    @DisplayName("Escenario 5: Exportar modificaciones en PDF")
    void exportarModificacionesPDF_conPeriodo_debeConsultarFiltrosYGenerarPDFValido() throws Exception {
        // given
        LocalDate fechaInicio = LocalDate.of(2026, 7, 1);
        LocalDate fechaFin = LocalDate.of(2026, 7, 31);

        Modifications modification = mock(Modifications.class);

        ModificationsResponseDTO dto = new ModificationsResponseDTO();
        dto.setId(1);
        dto.setDateTime(LocalDateTime.of(2026, 7, 11, 9, 15));
        dto.setEditedClass("Producto");
        dto.setName("ARROZ");
        dto.setEditedAttribute("status");
        dto.setPreviousValue("ACTIVO");
        dto.setNewValue("INACTIVO");
        dto.setUsername("JUAN PEREZ");

        when(modificationsRepositoryPort.listByPeriod("2026-07-01", "2026-07-31"))
                .thenReturn(List.of(modification));

        when(empresaConfigRepositoryPort.obtener())
                .thenReturn(crearEmpresaConfig());

        when(modificationsMapper.toResponseDTO(modification))
                .thenReturn(dto);

        // when
        byte[] pdf = exportModificationsPDFService.exportar(fechaInicio, fechaFin);

        // then
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);

        String text = extraerTextoPDF(pdf);

        assertTrue(text.contains("Comedor Municipal"));
        assertTrue(text.contains("Producto"));
        assertTrue(text.contains("ARROZ"));
        assertTrue(text.contains("status"));
        assertTrue(text.contains("ACTIVO"));
        assertTrue(text.contains("INACTIVO"));
        assertTrue(text.contains("JUAN PEREZ"));
        assertTrue(text.contains("Total de registros: 1"));

        verify(modificationsRepositoryPort)
                .listByPeriod("2026-07-01", "2026-07-31");

        verify(modificationsMapper).toResponseDTO(modification);
    }

    @Test
    @DisplayName("Escenario 6: Exportar documentos sin registros")
    void exportarDocumentos_sinRegistros_debeGenerarArchivosValidosSinErrores() throws Exception {
        // given
        LocalDate fechaInicio = LocalDate.of(2026, 7, 1);
        LocalDate fechaFin = LocalDate.of(2026, 7, 31);

        when(menuReportRepositoryPort.showMenuReport(fechaInicio, fechaFin))
                .thenReturn(List.of());

        when(transactionRepositoryPort.showTransaccionesByPeriod("2026-07-01", "2026-07-31"))
                .thenReturn(List.of());

        when(modificationsRepositoryPort.listByPeriod("2026-07-01", "2026-07-31"))
                .thenReturn(List.of());

        when(empresaConfigRepositoryPort.obtener())
                .thenReturn(crearEmpresaConfig());

        // when
        byte[] excel = exportReportExcelService.exportar(fechaInicio, fechaFin);
        byte[] pdfReportes = exportReportPDFService.exportar(fechaInicio, fechaFin);
        byte[] pdfTransacciones = exportTransactionsPDFService.exportar(fechaInicio, fechaFin);
        byte[] pdfModificaciones = exportModificationsPDFService.exportar(fechaInicio, fechaFin);

        // then
        assertNotNull(excel);
        assertNotNull(pdfReportes);
        assertNotNull(pdfTransacciones);
        assertNotNull(pdfModificaciones);

        assertTrue(excel.length > 0);
        assertTrue(pdfReportes.length > 0);
        assertTrue(pdfTransacciones.length > 0);
        assertTrue(pdfModificaciones.length > 0);

        String textTransacciones = extraerTextoPDF(pdfTransacciones);
        String textModificaciones = extraerTextoPDF(pdfModificaciones);

        assertTrue(textTransacciones.contains("Total de registros: 0"));
        assertTrue(textModificaciones.contains("Total de registros: 0"));

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel))) {
            assertNotNull(workbook.getSheet("Resumen General"));
            assertNotNull(workbook.getSheet("Reportes"));
            assertNotNull(workbook.getSheet("Insumos"));
            assertNotNull(workbook.getSheet("Beneficiarios"));
        }
    }

    private String extraerTextoPDF(byte[] pdfBytes) throws Exception {
        StringBuilder text = new StringBuilder();

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(pdfBytes))
        )) {
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i)));
                text.append("\n");
            }
        }

        return text.toString();
    }

    private MenuReport crearMenuReportBase() {
        Product arroz = crearProducto(1, "ARROZ", "KG");

        DishSupply supply = new DishSupply();
        supply.setId(1);
        supply.setProduct(arroz);
        supply.setQuantityNeeded(new BigDecimal("0.50"));

        DishMenu dishMenu = new DishMenu();
        dishMenu.setId(1);
        dishMenu.setName("ARROZ CON POLLO");
        dishMenu.setStatus(Status.ACTIVO);
        dishMenu.setSupplies(List.of(supply));

        MenuReport reporte = new MenuReport();
        reporte.setId(1);
        reporte.setDate(LocalDate.of(2026, 7, 10));
        reporte.setDishMenu(dishMenu);
        reporte.setQuantityPrepared(10);
        reporte.setQuantityRemaining(3);
        reporte.setTotalEarned(new BigDecimal("25.00"));
        reporte.setTotalSpent(new BigDecimal("12.50"));
        reporte.setStatus(StatusMenuReport.CERRADO);

        return reporte;
    }

    private BeneficiaryControl crearBeneficiaryControl() {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setId(1);
        beneficiary.setName("MARIA");
        beneficiary.setLastname("LOPEZ");
        beneficiary.setDni("12345678");
        beneficiary.setStatus(Status.ACTIVO);

        BeneficiaryControl control = new BeneficiaryControl();
        control.setId(1);
        control.setBeneficiario(beneficiary);
        control.setMenusAmount(2);
        control.setPaid(true);
        control.setReceived(true);
        control.setPayMethod(PaymentMethod.EFECTIVO);
        control.setMenuPrice(new BigDecimal("3.50"));

        return control;
    }

    private Product crearProducto(Integer id, String name, String unit) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setUnit(unit);
        product.setStatus(Status.ACTIVO);
        product.setStock(BigDecimal.ZERO);
        return product;
    }

    private EmpresaConfig crearEmpresaConfig() {
        EmpresaConfig config = new EmpresaConfig();
        config.setId(1);
        config.setNombre("Comedor Municipal");
        config.setDescripcion("Municipalidad Provincial de Trujillo");
        config.setLogoBase64(null);
        return config;
    }
}