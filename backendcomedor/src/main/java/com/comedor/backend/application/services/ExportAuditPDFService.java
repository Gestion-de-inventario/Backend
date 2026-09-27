package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.AuditMapper;
import com.comedor.backend.application.ports.in.ExportAuditPDFUseCase;
import com.comedor.backend.application.ports.out.AuditRepositoryPort;
import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.domain.model.Audit;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuditResponseDTO;
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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExportAuditPDFService implements ExportAuditPDFUseCase {

    private final AuditRepositoryPort auditRepositoryPort;
    private final AuditMapper auditMapper;
    private final EmpresaConfigRepositoryPort empresaConfigRepositoryPort;

    public ExportAuditPDFService(
            AuditRepositoryPort auditRepositoryPort,
            AuditMapper auditMapper,
            EmpresaConfigRepositoryPort empresaConfigRepositoryPort) {

        this.auditRepositoryPort = auditRepositoryPort;
        this.auditMapper = auditMapper;
        this.empresaConfigRepositoryPort = empresaConfigRepositoryPort;
    }

    @Override
    public byte[] exportar(LocalDate fechaInicio, LocalDate fechaFin) {

        List<Audit> auditorias = auditRepositoryPort.listByPeriod(
                fechaInicio != null ? fechaInicio.toString() : null,
                fechaFin != null ? fechaFin.toString() : null
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);

            Document doc = new Document(pdf, PageSize.A4.rotate());
            doc.setMargins(40, 40, 40, 40);

            // =========================
            // HEADER
            // =========================

            EmpresaConfig config = empresaConfigRepositoryPort.obtener();

            try {

                byte[] logoBytes;

                if (config.getLogoBase64() != null
                        && !config.getLogoBase64().isBlank()) {

                    logoBytes = Base64.getDecoder()
                            .decode(config.getLogoBase64());

                } else {

                    ClassPathResource logoResource =
                            new ClassPathResource("static/logo.jpg");

                    logoBytes = logoResource
                            .getInputStream()
                            .readAllBytes();
                }

                ImageData imageData =
                        ImageDataFactory.create(logoBytes);

                Image logo = new Image(imageData)
                        .setWidth(60)
                        .setHeight(60);

                Table header = new Table(
                        UnitValue.createPercentArray(
                                new float[]{1, 4}
                        )
                ).useAllAvailableWidth();

                header.addCell(
                        new Cell()
                                .add(logo)
                                .setBorder(Border.NO_BORDER)
                                .setVerticalAlignment(
                                        VerticalAlignment.MIDDLE
                                )
                );

                header.addCell(
                        new Cell()
                                .add(
                                        new Paragraph(config.getNombre())
                                                .setBold()
                                                .setFontSize(13)
                                                .setFontColor(
                                                        new DeviceRgb(
                                                                48, 63, 159
                                                        )
                                                )
                                )
                                .add(
                                        new Paragraph(
                                                config.getDescripcion() != null
                                                        ? config.getDescripcion()
                                                        : ""
                                        )
                                                .setFontSize(11)
                                                .setFontColor(
                                                        new DeviceRgb(
                                                                100, 100, 100
                                                        )
                                                )
                                )
                                .setBorder(Border.NO_BORDER)
                                .setVerticalAlignment(
                                        VerticalAlignment.MIDDLE
                                )
                );

                doc.add(header);

            } catch (Exception e) {

                doc.add(
                        new Paragraph(config.getNombre())
                                .setBold()
                                .setFontSize(16)
                );
            }

            // =========================
            // PERIODO
            // =========================

            String periodo =
                    (fechaInicio != null
                            ? fechaInicio.toString()
                            : "Inicio")
                            + " — " +
                            (fechaFin != null
                                    ? fechaFin.toString()
                                    : "Hoy");

            doc.add(
                    new Paragraph("Período: " + periodo)
                            .setFontSize(10)
                            .setFontColor(
                                    new DeviceRgb(100, 100, 100)
                            )
            );

            doc.add(
                    new LineSeparator(new SolidLine())
                            .setMarginTop(6)
                            .setMarginBottom(12)
            );

            // =========================
            // TABLA
            // =========================

            Table table = new Table(
                    UnitValue.createPercentArray(
                            new float[]{
                                    1.5f, // Fecha
                                    1.2f, // Acción
                                    1.3f, // Entidad
                                    1.8f, // Nombre
                                    4.0f, // Detalles
                                    2.0f  // Usuario
                            }
                    )
            ).useAllAvailableWidth();

            String[] headers = {
                    "Fecha",
                    "Acción",
                    "Entidad",
                    "Nombre",
                    "Detalles",
                    "Usuario"
            };

            for (String h : headers) {

                table.addHeaderCell(
                        new Cell()
                                .add(
                                        new Paragraph(h)
                                                .setBold()
                                )
                                .setBackgroundColor(
                                        new DeviceRgb(
                                                48, 63, 159
                                        )
                                )
                                .setFontColor(
                                        ColorConstants.WHITE
                                )
                );
            }

            // =========================
            // REGISTROS
            // =========================

            for (Audit audit : auditorias) {

                AuditResponseDTO dto =
                        auditMapper.toResponseDTO(audit);

                // Fecha
                table.addCell(
                        new Cell().add(
                                new Paragraph(
                                        dto.getDateTime() != null
                                                ? dto.getDateTime().toString()
                                                : "-"
                                )
                        )
                );

                // Acción
                table.addCell(
                        new Cell().add(
                                new Paragraph(
                                        formatAction(dto.getAction())
                                )
                        )
                );

                // Entidad
                table.addCell(
                        new Cell().add(
                                new Paragraph(
                                        dto.getEntityType() != null
                                                ? dto.getEntityType()
                                                : "-"
                                )
                        )
                );

                // Nombre
                table.addCell(
                        new Cell().add(
                                new Paragraph(
                                        dto.getEntityName() != null
                                                ? dto.getEntityName()
                                                : "-"
                                )
                        )
                );

                // Detalles
                table.addCell(
                        new Cell().add(
                                new Paragraph(
                                        formatDetails(
                                                dto.getAction(),
                                                dto.getDetails()
                                        )
                                )
                        )
                );

                // Usuario
                table.addCell(
                        new Cell().add(
                                new Paragraph(
                                        dto.getUsername() != null
                                                ? dto.getUsername()
                                                : "-"
                                )
                        )
                );
            }

            doc.add(table);

            // =========================
            // TOTAL
            // =========================

            doc.add(
                    new Paragraph(
                            "\nTotal de registros: "
                                    + auditorias.size()
                    )
                            .setFontSize(10)
            );

            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al generar PDF de auditoría: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private String formatAction(AuditAction action) {

        if (action == null) {
            return "-";
        }

        return switch (action) {
            case CREACION -> "Creación";
            case MODIFICACION -> "Modificación";
        };
    }

    private String formatDetails(
            AuditAction action,
            Map<String, Object> details) {

        if (details == null || details.isEmpty()) {
            return "-";
        }

        if (action == AuditAction.MODIFICACION) {

            String attribute = String.valueOf(
                    details.getOrDefault("attribute", "-")
            );

            String previousValue = String.valueOf(
                    details.getOrDefault("previousValue", "-")
            );

            String newValue = String.valueOf(
                    details.getOrDefault("newValue", "-")
            );

            return "Se ha modificado: " + attribute
                    + "\nValor antes: " + previousValue
                    + "\nNuevo valor: " + newValue;
        }

        // CREACION
        return details.entrySet()
                .stream()
                .map(entry ->
                        entry.getKey() + ": "
                                + String.valueOf(entry.getValue())
                )
                .collect(Collectors.joining("\n"));
    }
}