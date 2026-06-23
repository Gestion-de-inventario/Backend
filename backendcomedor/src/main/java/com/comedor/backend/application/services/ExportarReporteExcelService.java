package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ExportarReporteExcelUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.MenuReport;
import com.itextpdf.io.source.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.util.List;

public class ExportarReporteExcelService implements ExportarReporteExcelUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    public ExportarReporteExcelService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.beneficiaryControlRepositoryPort = beneficiaryControlRepositoryPort;
    }


    @Override
    public byte[] exportar(int reporteId) {
        MenuReport reporte = menuReportRepositoryPort.findById(reporteId);
        List<BeneficiaryControl> beneficiarios = beneficiaryControlRepositoryPort.findByReporteId(reporteId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // ── HOJA 1: RESUMEN ──
            Sheet resumen = workbook.createSheet("Resumen");
            String[][] resumenData = {
                    {"Fecha", reporte.getDate().toString()},
                    {"Plato", reporte.getDishMenu() != null ? reporte.getDishMenu().getName() : "-"},
                    {"Platos preparados", String.valueOf(reporte.getQuantityPrepared())},
                    {"Platos entregados", String.valueOf(reporte.getQuantityPrepared() - reporte.getQuantityRemaining())},
                    {"Platos sobrantes", String.valueOf(reporte.getQuantityRemaining())},
                    {"Total recaudado", "S/ " + reporte.getTotalEarned()},
                    {"Total gastado", "S/ " + reporte.getTotalSpent()},
                    {"Estado", reporte.getStatus().name()},
            };
            for (int i = 0; i < resumenData.length; i++) {
                Row row = resumen.createRow(i);
                row.createCell(0).setCellValue(resumenData[i][0]);
                row.createCell(1).setCellValue(resumenData[i][1]);
            }
            resumen.autoSizeColumn(0);
            resumen.autoSizeColumn(1);

            // ── HOJA 2: INSUMOS ──
            Sheet insumos = workbook.createSheet("Insumos");
            String[] insumosHeader = {"Producto", "Cantidad por plato", "Unidad", "Total usado"};
            Row insumosHeaderRow = insumos.createRow(0);
            for (int i = 0; i < insumosHeader.length; i++) {
                Cell cell = insumosHeaderRow.createCell(i);
                cell.setCellValue(insumosHeader[i]);
                cell.setCellStyle(headerStyle);
            }
            if (reporte.getDishMenu() != null) {
                int rowIdx = 1;
                for (DishSupply supply : reporte.getDishMenu().getSupplies()) {
                    BigDecimal totalUsado = supply.getQuantityNeeded()
                            .multiply(BigDecimal.valueOf(reporte.getQuantityPrepared()));
                    Row row = insumos.createRow(rowIdx++);
                    row.createCell(0).setCellValue(supply.getProduct().getName());
                    row.createCell(1).setCellValue(supply.getQuantityNeeded().doubleValue());
                    row.createCell(2).setCellValue(supply.getProduct().getUnit());
                    row.createCell(3).setCellValue(totalUsado.doubleValue());
                }
            }
            for (int i = 0; i < 4; i++) insumos.autoSizeColumn(i);

            // ── HOJA 3: BENEFICIARIOS ──
            Sheet beneSheet = workbook.createSheet("Beneficiarios");
            String[] beneHeader = {"Nombre", "Apellido", "DNI", "Menús", "Pagó", "Método pago", "Precio menú", "Total"};
            Row beneHeaderRow = beneSheet.createRow(0);
            for (int i = 0; i < beneHeader.length; i++) {
                Cell cell = beneHeaderRow.createCell(i);
                cell.setCellValue(beneHeader[i]);
                cell.setCellStyle(headerStyle);
            }
            int beneIdx = 1;
            for (BeneficiaryControl b : beneficiarios) {
                Row row = beneSheet.createRow(beneIdx++);
                row.createCell(0).setCellValue(b.getBeneficiario().getName());
                row.createCell(1).setCellValue(b.getBeneficiario().getLastname());
                row.createCell(2).setCellValue(b.getBeneficiario().getDni());
                row.createCell(3).setCellValue(b.getMenusAmount());
                row.createCell(4).setCellValue(b.getPaid() ? "Sí" : "No");
                row.createCell(5).setCellValue(b.getPayMethod() != null ? b.getPayMethod().name() : "-");
                row.createCell(6).setCellValue(b.getMenuPrice() != null ? b.getMenuPrice().doubleValue() : 0);
                row.createCell(7).setCellValue(b.getMenuPrice() != null ?
                        b.getMenuPrice().multiply(BigDecimal.valueOf(b.getMenusAmount())).doubleValue() : 0);
            }
            for (int i = 0; i < 8; i++) beneSheet.autoSizeColumn(i);

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }
}
