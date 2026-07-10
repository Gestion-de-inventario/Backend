package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.TransactionMapper;
import com.comedor.backend.application.ports.in.ExportTransactionsPDFUseCase;
import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.application.ports.out.TransactionRepositoryPort;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.domain.model.Transactions;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TransactionResponseDTO;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

public class ExportTransactionsPDFService implements ExportTransactionsPDFUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionMapper mapper;
    private final EmpresaConfigRepositoryPort empresaConfigRepositoryPort;


    public ExportTransactionsPDFService(TransactionRepositoryPort repository, TransactionMapper mapper, EmpresaConfigRepositoryPort empresaConfigRepositoryPort) {
        this.repository = repository;
        this.mapper = mapper;
        this.empresaConfigRepositoryPort = empresaConfigRepositoryPort;
    }

    @Override
    public byte[] exportar(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Transactions> transacciones = repository.showTransaccionesByPeriod(
                fechaInicio != null ? fechaInicio.toString() : null,
                fechaFin != null ? fechaFin.toString() : null
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(40, 40, 40, 40);

            // Header
            EmpresaConfig config = empresaConfigRepositoryPort.obtener();

            try {
                byte[] logoBytes;
                if (config.getLogoBase64() != null && !config.getLogoBase64().isBlank()) {
                    logoBytes = Base64.getDecoder().decode(config.getLogoBase64());
                } else {
                    ClassPathResource logoResource = new ClassPathResource("static/logo.jpg");
                    logoBytes = logoResource.getInputStream().readAllBytes();
                }

                ImageData imageData = ImageDataFactory.create(logoBytes);
                Image logo = new Image(imageData).setWidth(60).setHeight(60);
                Table header = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
                header.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                header.addCell(new Cell()
                        .add(new Paragraph(config.getNombre())
                                .setBold().setFontSize(13).setFontColor(new DeviceRgb(48, 63, 159)))
                        .add(new Paragraph(config.getDescripcion() != null ? config.getDescripcion() : "")
                                .setFontSize(11).setFontColor(new DeviceRgb(100, 100, 100)))
                        .setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                doc.add(header);
            } catch (Exception e) {
                doc.add(new Paragraph(config.getNombre()).setBold().setFontSize(16));
            }

            // Periodo
            String periodo = (fechaInicio != null ? fechaInicio.toString() : "Inicio") +
                    " — " +
                    (fechaFin != null ? fechaFin.toString() : "Hoy");
            doc.add(new Paragraph("Período: " + periodo).setFontSize(10).setFontColor(new DeviceRgb(100,100,100)));
            doc.add(new LineSeparator(new SolidLine()).setMarginTop(6).setMarginBottom(12));

            // Tabla
            Table table = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1, 1, 1, 1, 2})).useAllAvailableWidth();
            String[] headers = {"Fecha", "Producto","Tipo Producto", "Tipo de transaccion","Fuente", "Cantidad", "Stock Final", "Usuario"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setBold())
                        .setBackgroundColor(new DeviceRgb(48, 63, 159))
                        .setFontColor(ColorConstants.WHITE));
            }

            for (Transactions t : transacciones) {
                TransactionResponseDTO dto = mapper.toDTO(t);
                table.addCell(new Cell().add(new Paragraph(dto.getDateTime().toString())));
                table.addCell(new Cell().add(new Paragraph(dto.getItemName())));
                table.addCell(new Cell().add(new Paragraph(dto.getReferenceType().toString())));
                table.addCell(new Cell().add(new Paragraph(dto.getType().name())));
                table.addCell(new Cell().add(new Paragraph(dto.getSource().toString())));
                table.addCell(new Cell().add(new Paragraph(dto.getAmount().toString())));
                table.addCell(new Cell().add(new Paragraph(dto.getFinalStock().toString())));
                table.addCell(new Cell().add(new Paragraph(dto.getPersonaName() + " " + dto.getPersonaLastName())));
            }
            doc.add(table);
            doc.add(new Paragraph("\nTotal de registros: " + transacciones.size()).setFontSize(10));
            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
}
