package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ModificationsMapper;
import com.comedor.backend.application.ports.in.ExportModificationsPDFUseCase;
import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
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
import java.util.List;

public class ExportModificationsPDFService implements ExportModificationsPDFUseCase {

    private final ModificationsRepositoryPort repository;
    private final ModificationsMapper mapper;

    public ExportModificationsPDFService(ModificationsRepositoryPort repository, ModificationsMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public byte[] exportar(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Modifications> modificaciones = repository.listByPeriod(
                fechaInicio != null ? fechaInicio.toString() : null,
                fechaFin != null ? fechaFin.toString() : null
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(40, 40, 40, 40);

            // Header
            try {
                ClassPathResource logoResource = new ClassPathResource("static/logo.jpg");
                ImageData imageData = ImageDataFactory.create(logoResource.getInputStream().readAllBytes());
                Image logo = new Image(imageData).setWidth(60).setHeight(60);
                Table header = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
                header.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                header.addCell(new Cell()
                        .add(new Paragraph("COMEDOR MUNICIPAL\nMUNICIPALIDAD PROVINCIAL DE TRUJILLO")
                                .setBold().setFontSize(13).setFontColor(new DeviceRgb(48, 63, 159)))
                        .add(new Paragraph("HISTORIAL DE MODIFICACIONES")
                                .setFontSize(11).setFontColor(new DeviceRgb(100, 100, 100)))
                        .setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                doc.add(header);
            } catch (Exception e) {
                doc.add(new Paragraph("HISTORIAL DE MODIFICACIONES").setBold().setFontSize(16));
            }

            String periodo = (fechaInicio != null ? fechaInicio.toString() : "Inicio") +
                    " — " +
                    (fechaFin != null ? fechaFin.toString() : "Hoy");
            doc.add(new Paragraph("Período: " + periodo).setFontSize(10).setFontColor(new DeviceRgb(100,100,100)));
            doc.add(new LineSeparator(new SolidLine()).setMarginTop(6).setMarginBottom(12));

            Table table = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1.2f, 1.2f, 1.5f, 1.5f, 1.5f})).useAllAvailableWidth();
            String[] headers = {"Fecha", "Entidad", "Campo", "Valor anterior", "Valor nuevo", "Usuario"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setBold())
                        .setBackgroundColor(new DeviceRgb(48, 63, 159))
                        .setFontColor(ColorConstants.WHITE));
            }

            for (Modifications m : modificaciones) {
                ModificationsResponseDTO dto = mapper.toResponseDTO(m);
                table.addCell(new Cell().add(new Paragraph(dto.getDateTime().toString())));
                table.addCell(new Cell().add(new Paragraph(dto.getEditedClass())));
                table.addCell(new Cell().add(new Paragraph(dto.getEditedAttribute())));
                table.addCell(new Cell().add(new Paragraph(dto.getPreviousValue() != null ? dto.getPreviousValue() : "-")));
                table.addCell(new Cell().add(new Paragraph(dto.getNewValue() != null ? dto.getNewValue() : "-")));
                table.addCell(new Cell().add(new Paragraph(dto.getUsername())));
            }
            doc.add(table);
            doc.add(new Paragraph("\nTotal de registros: " + modificaciones.size()).setFontSize(10));
            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
}
