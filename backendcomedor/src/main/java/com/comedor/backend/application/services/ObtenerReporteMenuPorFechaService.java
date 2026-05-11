package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.ReporteMenuMapper;
import com.comedor.backend.application.ports.in.ObtenerReporteMenuPorFechaUseCase;
import com.comedor.backend.application.ports.in.ObtenerResumenReporteMenuUseCase;
import com.comedor.backend.application.ports.out.PersonaRepositoryPort;
import com.comedor.backend.application.ports.out.ReporteMenuRepositoryPort;
import com.comedor.backend.domain.model.ControlBeneficiario;
import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.domain.model.Registro;
import com.comedor.backend.domain.model.ReporteMenu;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public class ObtenerReporteMenuPorFechaService implements ObtenerReporteMenuPorFechaUseCase {

    private final ReporteMenuRepositoryPort reporteMenuRepositoryPort;
    private final ReporteMenuMapper reporteMenuMapper;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase;
    public ObtenerReporteMenuPorFechaService(ReporteMenuRepositoryPort reporteMenuRepositoryPort, ReporteMenuMapper reporteMenuMapper, PersonaRepositoryPort personaRepositoryPort, ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase) {
        this.reporteMenuRepositoryPort = reporteMenuRepositoryPort;
        this.reporteMenuMapper = reporteMenuMapper;
        this.personaRepositoryPort = personaRepositoryPort;
        this.obtenerResumenReporteMenuUseCase = obtenerResumenReporteMenuUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleReporteMenuResponseDTO obtenerPorFecha(
            LocalDate fecha
    ) {

        ReporteMenu reporte =reporteMenuRepositoryPort.findByDate(fecha);

        List<Persona> cocineras =personaRepositoryPort.findAllByIds(reporte.getCooks());

        List<Registro> registroProductos = reporte.getProductRecord();

        List<ControlBeneficiario> registroBeneficiarios = reporte.getBeneficiariosRecord();

        ResumenReporteMenuResponseDTO resumenReporteMenu = obtenerResumenReporteMenuUseCase.obtenerResumen(reporte.getId());


        return reporteMenuMapper.toDetalleDto(
                reporte,
                cocineras,
                registroProductos,
                registroBeneficiarios,
                resumenReporteMenu);
    }
}
