package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditMenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.MenuReportRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/menu_report")
@RequiredArgsConstructor
public class MenuRecordController {

    private final CreateMenuReportUseCase createMenuReportUseCase;
    private final AddRecordBeneficiaryUseCase addRecordBeneficiaryUseCase;
    private final DeleteBeneficiaryRecordUseCase deleteBeneficiaryRecordUseCase;
    private final EditBeneficiaryRecordUseCase editBeneficiaryRecordUseCase;
    private final EditMenuReportUseCase editMenuReportUseCase;
    private final ListMenuReportDetailUseCase listMenuReportDetailUseCase;
    private final ListMenuReportUseCase listMenuReportUseCase;
    private final GetMenuReportByIdUseCase getMenuReportByIdUseCase;
    private final ExportReportPDFUseCase exportReportPDFUseCase;
    private final ExportReportExcelUseCase exportReportExcelUseCase;

    @PreAuthorize("hasAuthority('MENU_REPORT_CREATE_REPORT')")
    @PostMapping("/create")
    public MenuReportResponseDTO createReporteMenu(@RequestBody MenuReportRequestDTO request) {
        return createMenuReportUseCase.crearReporteMenu(request);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_GET_BY_DATE')")
    @GetMapping("/detail/list")
    public ListMenuReportDetailResponseDTO obtenerPorFecha(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {
        return listMenuReportDetailUseCase.list(startDate, endDate);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_LIST_ALL')")
    @GetMapping("/list")
    public Page<MenuReportResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return listMenuReportUseCase.list(
                page,
                size,
                startDate,
                endDate
        );
    }
    @PreAuthorize("hasAuthority('MENU_REPORT_LIST_ALL')")
    @GetMapping("/{id}")
    public MenuReportResponseDTO getById(@PathVariable int id)
    {
        return getMenuReportByIdUseCase.getMenuReportById(id);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EDIT')")
    @PutMapping("/{id}/edit")
    public MenuReportResponseDTO edit(
            @PathVariable int id,
            @RequestBody EditMenuReportRequestDTO request
    ) {
        return editMenuReportUseCase.editMenuReport(id, request);
    }




    @PreAuthorize("hasAuthority('MENU_REPORT_ADD_BENEFICIARY')")
    @PostMapping("/{id}/beneficiaries")
    public BeneficiaryRecordResponseDTO agregarBeneficiario(
            @PathVariable int id,
            @RequestBody ControlBeneficiarioRequestDTO dto) {
        return addRecordBeneficiaryUseCase.agregarRegistroBeneficiario(id, dto);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EDIT_BENEFICIARY')")
    @PatchMapping("/{reporteId}/beneficiaries/{controlId}")
    public BeneficiaryRecordResponseDTO editarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId,
            @RequestBody ControlBeneficiarioRequestDTO dto) {
        return editBeneficiaryRecordUseCase.editarRegistroBeneficiario(reporteId, controlId, dto);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_REMOVE_BENEFICIARY')")
    @DeleteMapping("/{reporteId}/beneficiaries/{controlId}")
    public ResponseEntity<Void> eliminarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId) {
        deleteBeneficiaryRecordUseCase.eliminarRegistroBeneficiario(reporteId, controlId);
        return ResponseEntity.noContent().build();
    }



    @PreAuthorize("hasAuthority('MENU_REPORT_EXPORT')")
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable int id) {
        byte[] pdf = exportReportPDFUseCase.exportar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @PreAuthorize("hasAuthority('MENU_REPORT_EXPORT')")
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarRangoPDF(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        byte[] pdf = exportReportPDFUseCase.exportar(startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte-" + startDate + "-" + endDate + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EXPORT')")
    @GetMapping("/{id}/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable int id) {
        byte[] excel = exportReportExcelUseCase.exportar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + id + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EXPORT')")
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportarRangoExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {
        byte[] excel = exportReportExcelUseCase.exportar(startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + startDate + "-" + endDate +".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
