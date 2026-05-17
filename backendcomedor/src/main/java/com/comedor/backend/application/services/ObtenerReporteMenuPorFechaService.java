package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.ObtenerReporteMenuPorFechaUseCase;
import com.comedor.backend.application.ports.in.ObtenerResumenReporteMenuUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.Record;
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
    public ObtenerReporteMenuPorFechaService(MenuReportRepositoryPort menuReportRepositoryPort, MenuReportMapper menuReportMapper, PersonRepositoryPort personRepositoryPort, ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.menuReportMapper = menuReportMapper;
        this.personRepositoryPort = personRepositoryPort;
        this.obtenerResumenReporteMenuUseCase = obtenerResumenReporteMenuUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleReporteMenuResponseDTO obtenerPorFecha(
            LocalDate fecha
    ) {

        MenuReport reporte = menuReportRepositoryPort.findByDate(fecha);

        List<Person> cocineras = personRepositoryPort.findAllByIds(reporte.getCooks());

        List<Record> productRecord = reporte.getProductRecord();

        List<BeneficiaryControl> registroBeneficiarios = reporte.getBeneficiariosRecord();

        ResumenReporteMenuResponseDTO resumenReporteMenu = obtenerResumenReporteMenuUseCase.obtenerResumen(reporte.getId());


        return menuReportMapper.toDetalleDto(
                reporte,
                cocineras,
                productRecord,
                registroBeneficiarios,
                resumenReporteMenu);
    }
}
