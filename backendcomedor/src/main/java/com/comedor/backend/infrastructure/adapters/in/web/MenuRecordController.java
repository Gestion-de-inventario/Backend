package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.services.GetMenuReporByIdService;
import com.comedor.backend.application.services.ListMenuReportService;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ReporteMenuRequestDTO;
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

    private final CrearReporteMenuUseCase crearReporteMenuUseCase;
    private final AgregarRegistroBeneficiarioUseCase agregarRegistroBeneficiarioUseCase;
    private final EliminarRegistroBeneficiarioUseCase eliminarRegistroBeneficiarioUseCase;
    private final EditarRegistroBeneficiarioUseCase editarRegistroBeneficiarioUseCase;
    private final ListMenuReportDetailUseCase listMenuReportDetailUseCase;
    private final ListMenuReportUseCase listMenuReportUseCase;
    private final GetMenuReportByIdUseCase getMenuReportByIdUseCase;
    private final ExportarReportePDFUseCase exportarReportePDFUseCase;
    private final ExportarReporteExcelUseCase exportarReporteExcelUseCase;

    @PreAuthorize("hasAuthority('MENU_REPORT_CREATE_REPORT')")
    @PostMapping("/create")
    public ReporteMenuResponseDTO createReporteMenu(@RequestBody ReporteMenuRequestDTO request) {
        return crearReporteMenuUseCase.crearReporteMenu(request);
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

    @PreAuthorize("hasAuthority('MENU_REPORT_LIST_ALL')") //MENU_REPORT_GET_SUMMARY
    @GetMapping("/list")
    public Page<ReporteMenuResponseDTO> list(
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
    public ReporteMenuResponseDTO getById( @PathVariable int id)
    {
        return getMenuReportByIdUseCase.getMenuReportById(id);
    }


    @PreAuthorize("hasAuthority('MENU_REPORT_ADD_BENEFICIARY')")
    @PostMapping("/{id}/beneficiaries")
    public RegistroBeneficiarioResponseDTO agregarBeneficiario(
            @PathVariable int id,
            @RequestBody ControlBeneficiarioRequestDTO dto) {
        return agregarRegistroBeneficiarioUseCase.agregarRegistroBeneficiario(id, dto);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EDIT_BENEFICIARY')")
    @PatchMapping("/{reporteId}/beneficiaries/{controlId}")
    public RegistroBeneficiarioResponseDTO editarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId,
            @RequestBody ControlBeneficiarioRequestDTO dto) {
        return editarRegistroBeneficiarioUseCase.editarRegistroBeneficiario(reporteId, controlId, dto);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_REMOVE_BENEFICIARY')")
    @DeleteMapping("/{reporteId}/beneficiaries/{controlId}")
    public ResponseEntity<Void> eliminarBeneficiario(
            @PathVariable int reporteId,
            @PathVariable int controlId) {
        eliminarRegistroBeneficiarioUseCase.eliminarRegistroBeneficiario(reporteId, controlId);
        return ResponseEntity.noContent().build();
    }



    @PreAuthorize("hasAuthority('MENU_REPORT_EXPORT')")
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable int id) {
        byte[] pdf = exportarReportePDFUseCase.exportar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PreAuthorize("hasAuthority('MENU_REPORT_EXPORT')")
    @GetMapping("/{id}/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable int id) {
        byte[] excel = exportarReporteExcelUseCase.exportar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + id + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
