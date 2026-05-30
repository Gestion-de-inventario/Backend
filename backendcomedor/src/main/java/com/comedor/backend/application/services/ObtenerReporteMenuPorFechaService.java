package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.ObtenerReporteMenuPorFechaUseCase;
import com.comedor.backend.application.ports.in.ObtenerResumenReporteMenuUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.StockMovement; // <-- Importación actualizada (antes Record)
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public class ObtenerReporteMenuPorFechaService implements ObtenerReporteMenuPorFechaUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final MenuReportMapper menuReportMapper;
    private final PersonRepositoryPort personRepositoryPort;
    private final ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase;

    public ObtenerReporteMenuPorFechaService(MenuReportRepositoryPort menuReportRepositoryPort,
                                             MenuReportMapper menuReportMapper,
                                             PersonRepositoryPort personRepositoryPort,
                                             ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.menuReportMapper = menuReportMapper;
        this.personRepositoryPort = personRepositoryPort;
        this.obtenerResumenReporteMenuUseCase = obtenerResumenReporteMenuUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleReporteMenuResponseDTO obtenerPorFecha(LocalDate fecha) {

        MenuReport reporte = menuReportRepositoryPort.findByDate(fecha);

        if (reporte == null) {
            return null;
        }

        // 1. Ahora se obtiene el nombre desde la entidad DishMenu
        String menu = reporte.getDishMenu() != null ? reporte.getDishMenu().getName() : "";

        List<Person> cocineras = personRepositoryPort.findAllByIds(reporte.getCooks());

        // 2. Se reemplaza List<Record> por List<StockMovement>
        List<StockMovement> stockMovements = reporte.getStockMovements();

        // 3. Se actualiza al nuevo nombre de la lista de beneficiarios
        List<BeneficiaryControl> registroBeneficiarios = reporte.getBeneficiaryControls();

        ResumenReporteMenuResponseDTO resumenReporteMenu = obtenerResumenReporteMenuUseCase.obtenerResumen(reporte.getId());

        return menuReportMapper.toDetalleDto(
                reporte,
                menu,
                cocineras,
                stockMovements, // <-- Pasamos la nueva variable aquí
                registroBeneficiarios,
                resumenReporteMenu);
    }
}