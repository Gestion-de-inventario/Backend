package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ExportReportExcelUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryControlRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.DishSupply;
import com.comedor.backend.domain.model.MenuReport;
import com.itextpdf.io.source.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExportReportExcelService implements ExportReportExcelUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort;

    public ExportReportExcelService(MenuReportRepositoryPort menuReportRepositoryPort, BeneficiaryControlRepositoryPort beneficiaryControlRepositoryPort) {
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

    @Override
    public byte[] exportar(LocalDate startDate, LocalDate endDate) {

        List<MenuReport> reportes =
                menuReportRepositoryPort.showMenuReport(startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);

            BigDecimal totalEarnedGlobal = BigDecimal.ZERO;
            BigDecimal totalSpentGlobal = BigDecimal.ZERO;
            int totalPreparadosGlobal = 0;
            int totalEntregadosGlobal = 0;
            int totalSobrantesGlobal = 0;

            for (MenuReport reporte : reportes) {
                totalEarnedGlobal = totalEarnedGlobal.add(
                        reporte.getTotalEarned() != null
                                ? reporte.getTotalEarned()
                                : BigDecimal.ZERO
                );

                totalSpentGlobal = totalSpentGlobal.add(
                        reporte.getTotalSpent() != null
                                ? reporte.getTotalSpent()
                                : BigDecimal.ZERO
                );

                totalPreparadosGlobal += reporte.getQuantityPrepared();
                totalSobrantesGlobal += reporte.getQuantityRemaining();
                totalEntregadosGlobal += reporte.getQuantityPrepared() - reporte.getQuantityRemaining();
            }

            // ─────────────────────────────
            // HOJA 1: RESUMEN GENERAL
            // ─────────────────────────────
            Sheet resumenGeneral = workbook.createSheet("Resumen General");

            Row titleRow = resumenGeneral.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE GENERAL DE MENÚS");
            titleCell.setCellStyle(titleStyle);

            String[][] resumenData = {
                    {"Fecha inicio", startDate != null ? startDate.toString() : "Sin inicio"},
                    {"Fecha fin", endDate != null ? endDate.toString() : "Sin fin"},
                    {"Cantidad de reportes", String.valueOf(reportes.size())},
                    {"Total platos preparados", String.valueOf(totalPreparadosGlobal)},
                    {"Total platos entregados", String.valueOf(totalEntregadosGlobal)},
                    {"Total platos sobrantes", String.valueOf(totalSobrantesGlobal)},
                    {"Total recaudado", "S/ " + totalEarnedGlobal},
                    {"Total gastado", "S/ " + totalSpentGlobal},
                    {"Balance", "S/ " + totalEarnedGlobal.subtract(totalSpentGlobal)}
            };

            int resumenRowIdx = 2;

            for (String[] data : resumenData) {
                Row row = resumenGeneral.createRow(resumenRowIdx++);
                row.createCell(0).setCellValue(data[0]);
                row.createCell(1).setCellValue(data[1]);
            }

            resumenGeneral.autoSizeColumn(0);
            resumenGeneral.autoSizeColumn(1);


            // ─────────────────────────────
            // HOJA 2: REPORTES
            // ─────────────────────────────
            Sheet reportesSheet = workbook.createSheet("Reportes");

            String[] reportesHeader = {
                    "ID Reporte",
                    "Fecha",
                    "Plato",
                    "Estado",
                    "Preparados",
                    "Entregados",
                    "Sobrantes",
                    "Total recaudado",
                    "Total gastado",
                    "Balance"
            };

            Row reportesHeaderRow = reportesSheet.createRow(0);

            for (int i = 0; i < reportesHeader.length; i++) {
                Cell cell = reportesHeaderRow.createCell(i);
                cell.setCellValue(reportesHeader[i]);
                cell.setCellStyle(headerStyle);
            }

            int reporteRowIdx = 1;

            for (MenuReport reporte : reportes) {
                BigDecimal totalEarned = reporte.getTotalEarned() != null
                        ? reporte.getTotalEarned()
                        : BigDecimal.ZERO;

                BigDecimal totalSpent = reporte.getTotalSpent() != null
                        ? reporte.getTotalSpent()
                        : BigDecimal.ZERO;

                int entregados = reporte.getQuantityPrepared() - reporte.getQuantityRemaining();

                Row row = reportesSheet.createRow(reporteRowIdx++);

                row.createCell(0).setCellValue(reporte.getId());
                row.createCell(1).setCellValue(reporte.getDate() != null ? reporte.getDate().toString() : "-");
                row.createCell(2).setCellValue(
                        reporte.getDishMenu() != null
                                ? reporte.getDishMenu().getName()
                                : "-"
                );
                row.createCell(3).setCellValue(
                        reporte.getStatus() != null
                                ? reporte.getStatus().name()
                                : "-"
                );
                row.createCell(4).setCellValue(reporte.getQuantityPrepared());
                row.createCell(5).setCellValue(entregados);
                row.createCell(6).setCellValue(reporte.getQuantityRemaining());
                row.createCell(7).setCellValue(totalEarned.doubleValue());
                row.createCell(8).setCellValue(totalSpent.doubleValue());
                row.createCell(9).setCellValue(totalEarned.subtract(totalSpent).doubleValue());
            }

            for (int i = 0; i < reportesHeader.length; i++) {
                reportesSheet.autoSizeColumn(i);
            }


            // ─────────────────────────────
            // HOJA 3: INSUMOS
            // ─────────────────────────────
            Sheet insumosSheet = workbook.createSheet("Insumos");

            String[] insumosHeader = {
                    "ID Reporte",
                    "Fecha",
                    "Plato",
                    "Producto",
                    "Cantidad por plato",
                    "Unidad",
                    "Total usado"
            };

            Row insumosHeaderRow = insumosSheet.createRow(0);

            for (int i = 0; i < insumosHeader.length; i++) {
                Cell cell = insumosHeaderRow.createCell(i);
                cell.setCellValue(insumosHeader[i]);
                cell.setCellStyle(headerStyle);
            }

            int insumosRowIdx = 1;

            for (MenuReport reporte : reportes) {

                if (reporte.getDishMenu() == null ||
                        reporte.getDishMenu().getSupplies() == null ||
                        reporte.getDishMenu().getSupplies().isEmpty()) {
                    continue;
                }

                for (DishSupply supply : reporte.getDishMenu().getSupplies()) {

                    BigDecimal cantidadPorPlato = supply.getQuantityNeeded() != null
                            ? supply.getQuantityNeeded()
                            : BigDecimal.ZERO;

                    BigDecimal totalUsado = cantidadPorPlato.multiply(
                            BigDecimal.valueOf(reporte.getQuantityPrepared())
                    );

                    Row row = insumosSheet.createRow(insumosRowIdx++);

                    row.createCell(0).setCellValue(reporte.getId());
                    row.createCell(1).setCellValue(reporte.getDate() != null ? reporte.getDate().toString() : "-");
                    row.createCell(2).setCellValue(
                            reporte.getDishMenu() != null
                                    ? reporte.getDishMenu().getName()
                                    : "-"
                    );
                    row.createCell(3).setCellValue(
                            supply.getProduct() != null
                                    ? supply.getProduct().getName()
                                    : "-"
                    );
                    row.createCell(4).setCellValue(cantidadPorPlato.doubleValue());
                    row.createCell(5).setCellValue(
                            supply.getProduct() != null
                                    ? supply.getProduct().getUnit()
                                    : "-"
                    );
                    row.createCell(6).setCellValue(totalUsado.doubleValue());
                }
            }

            for (int i = 0; i < insumosHeader.length; i++) {
                insumosSheet.autoSizeColumn(i);
            }


            // ─────────────────────────────
            // HOJA 4: BENEFICIARIOS
            // ─────────────────────────────
            Sheet beneSheet = workbook.createSheet("Beneficiarios");

            String[] beneHeader = {
                    "ID Reporte",
                    "Fecha",
                    "Plato",
                    "Nombre",
                    "Apellido",
                    "DNI",
                    "Menús",
                    "Pagó",
                    "Método pago",
                    "Precio menú",
                    "Total"
            };

            Row beneHeaderRow = beneSheet.createRow(0);

            for (int i = 0; i < beneHeader.length; i++) {
                Cell cell = beneHeaderRow.createCell(i);
                cell.setCellValue(beneHeader[i]);
                cell.setCellStyle(headerStyle);
            }

            int beneRowIdx = 1;

            for (MenuReport reporte : reportes) {

                List<BeneficiaryControl> beneficiarios =
                        beneficiaryControlRepositoryPort.findByReporteId(reporte.getId());

                for (BeneficiaryControl b : beneficiarios) {

                    BigDecimal menuPrice = b.getMenuPrice() != null
                            ? b.getMenuPrice()
                            : BigDecimal.ZERO;

                    BigDecimal total = menuPrice.multiply(
                            BigDecimal.valueOf(b.getMenusAmount())
                    );

                    Row row = beneSheet.createRow(beneRowIdx++);

                    row.createCell(0).setCellValue(reporte.getId());
                    row.createCell(1).setCellValue(reporte.getDate() != null ? reporte.getDate().toString() : "-");
                    row.createCell(2).setCellValue(
                            reporte.getDishMenu() != null
                                    ? reporte.getDishMenu().getName()
                                    : "-"
                    );
                    row.createCell(3).setCellValue(
                            b.getBeneficiario() != null
                                    ? b.getBeneficiario().getName()
                                    : "-"
                    );
                    row.createCell(4).setCellValue(
                            b.getBeneficiario() != null
                                    ? b.getBeneficiario().getLastname()
                                    : "-"
                    );
                    row.createCell(5).setCellValue(
                            b.getBeneficiario() != null
                                    ? b.getBeneficiario().getDni()
                                    : "-"
                    );
                    row.createCell(6).setCellValue(b.getMenusAmount());
                    row.createCell(7).setCellValue(b.getPaid() ? "Sí" : "No");
                    row.createCell(8).setCellValue(
                            b.getPayMethod() != null
                                    ? b.getPayMethod().name()
                                    : "-"
                    );
                    row.createCell(9).setCellValue(menuPrice.doubleValue());
                    row.createCell(10).setCellValue(total.doubleValue());
                }
            }

            for (int i = 0; i < beneHeader.length; i++) {
                beneSheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
    }
    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);

        headerStyle.setFont(headerFont);

        return headerStyle;
    }

    private CellStyle createTitleStyle(Workbook workbook) {

        CellStyle titleStyle = workbook.createCellStyle();

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());

        titleStyle.setFont(titleFont);

        return titleStyle;
    }

}
