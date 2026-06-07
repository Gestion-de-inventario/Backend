package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ExportarReportePDFUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.MenuReport;
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
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;
import java.util.List;

public class ExportarReportePDFService implements ExportarReportePDFUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    public ExportarReportePDFService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
    }

    @Override
    public byte[] exportar(int reporteId) {
        MenuReport reporte = menuReportRepositoryPort.findById(reporteId);
        List<BeneficiaryControl> beneficiarios = beneficiaryControlRepositoryPort.findByReporteId(reporteId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(40, 40, 40, 40);

            // ── LOGO ──
            try {
                ClassPathResource logoResource = new ClassPathResource("static/logo.jpg");
                ImageData imageData = ImageDataFactory.create(logoResource.getInputStream().readAllBytes());
                Image logo = new Image(imageData).setWidth(70).setHeight(70);

                // Header con logo + título
                Table header = new Table(UnitValue.createPercentArray(new float[]{1, 4})).useAllAvailableWidth();
                header.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                header.addCell(new Cell()
                        .add(new Paragraph("COMEDOR MUNICIPAL\nMUNICIPALIDAD PROVINCIAL DE TRUJILLO")
                                .setBold().setFontSize(14).setFontColor(new DeviceRgb(48, 63, 159)))
                        .add(new Paragraph("REPORTE DIARIO DE MENÚ")
                                .setFontSize(11).setFontColor(new DeviceRgb(100, 100, 100)))
                        .setBorder(Border.NO_BORDER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
                doc.add(header);
            } catch (Exception e) {
                doc.add(new Paragraph("REPORTE DIARIO DE MENÚ").setBold().setFontSize(16));
            }

            // Línea separadora
            doc.add(new LineSeparator(new SolidLine()).setMarginTop(8).setMarginBottom(12));

            // ── INFO GENERAL ──
            doc.add(new Paragraph("INFORMACIÓN GENERAL").setBold().setFontSize(11)
                    .setFontColor(new DeviceRgb(48, 63, 159)));

            Table info = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            info.addCell(infoCell("Fecha", reporte.getDate().toString()));
            info.addCell(infoCell("Estado", reporte.getStatus().name()));
            info.addCell(infoCell("Plato", reporte.getDishMenu() != null ? reporte.getDishMenu().getName() : "-"));
            info.addCell(infoCell("Platos preparados", String.valueOf(reporte.getQuantityPrepared())));
            info.addCell(infoCell("Platos entregados", String.valueOf(reporte.getQuantityPrepared() - reporte.getQuantityRemaining())));
            info.addCell(infoCell("Platos sobrantes", String.valueOf(reporte.getQuantityRemaining())));
            info.addCell(infoCell("Total recaudado", "S/ " + reporte.getTotalEarned()));
            info.addCell(infoCell("Total gastado", "S/ " + reporte.getTotalSpent()));
            doc.add(info);
            doc.add(new Paragraph(" "));

            // ── INSUMOS USADOS ──
            doc.add(new Paragraph("INSUMOS UTILIZADOS").setBold().setFontSize(11)
                    .setFontColor(new DeviceRgb(48, 63, 159)));

            if (reporte.getDishMenu() != null && !reporte.getDishMenu().getSupplies().isEmpty()) {
                Table insumos = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1})).useAllAvailableWidth();
                insumos.addHeaderCell(headerCell("Producto"));
                insumos.addHeaderCell(headerCell("Por plato"));
                insumos.addHeaderCell(headerCell("Unidad"));
                insumos.addHeaderCell(headerCell("Total usado"));

                for (DishSupply supply : reporte.getDishMenu().getSupplies()) {
                    BigDecimal totalUsado = supply.getQuantityNeeded()
                            .multiply(BigDecimal.valueOf(reporte.getQuantityPrepared()));
                    insumos.addCell(new Cell().add(new Paragraph(supply.getProduct().getName())));
                    insumos.addCell(new Cell().add(new Paragraph(supply.getQuantityNeeded().toString())));
                    insumos.addCell(new Cell().add(new Paragraph(supply.getProduct().getUnit())));
                    insumos.addCell(new Cell().add(new Paragraph(totalUsado.toString())));
                }
                doc.add(insumos);
            }
            doc.add(new Paragraph(" "));

            // ── BENEFICIARIOS ──
            doc.add(new Paragraph("CONTROL DE BENEFICIARIOS").setBold().setFontSize(11)
                    .setFontColor(new DeviceRgb(48, 63, 159)));

            Table bene = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1, 1})).useAllAvailableWidth();
            bene.addHeaderCell(headerCell("Beneficiario"));
            bene.addHeaderCell(headerCell("DNI"));
            bene.addHeaderCell(headerCell("Menús"));
            bene.addHeaderCell(headerCell("Pagó"));
            bene.addHeaderCell(headerCell("Método"));

            for (BeneficiaryControl b : beneficiarios) {
                bene.addCell(new Cell().add(new Paragraph(
                        b.getBeneficiario().getName() + " " + b.getBeneficiario().getLastname())));
                bene.addCell(new Cell().add(new Paragraph(b.getBeneficiario().getDni())));
                bene.addCell(new Cell().add(new Paragraph(String.valueOf(b.getMenusAmount()))));
                bene.addCell(new Cell().add(new Paragraph(b.getPaid() ? "Sí" : "No")));
                bene.addCell(new Cell().add(new Paragraph(
                        b.getPayMethod() != null ? b.getPayMethod().name() : "-")));
            }
            doc.add(bene);
            doc.add(new Paragraph(" "));

            // ── ESPACIO PARA FIRMA ──
            doc.add(new LineSeparator(new SolidLine()).setMarginTop(20));
            Table firma = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            firma.addCell(new Cell().add(new Paragraph("\n\n_______________________\nFirma Responsable"))
                    .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            firma.addCell(new Cell().add(new Paragraph("\n\n_______________________\nSello del Comedor"))
                    .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
            doc.add(firma);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    private Cell infoCell(String label, String value) {
        return new Cell().add(new Paragraph(label + ": ").setBold().add(value)).setBorder(Border.NO_BORDER);
    }

    private Cell headerCell(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBackgroundColor(new DeviceRgb(48, 63, 159))
                .setFontColor(ColorConstants.WHITE);
    }

}
