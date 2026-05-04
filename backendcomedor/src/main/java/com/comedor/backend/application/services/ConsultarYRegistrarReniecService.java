package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ConsultarDatosPorDniUseCase;
import com.comedor.backend.application.ports.in.ConsultarYRegistrarReniecUseCase;
import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.application.ports.out.ReniecPort;
import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.domain.model.DatosPersonales;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.Optional;

public class ConsultarYRegistrarReniecService implements ConsultarYRegistrarReniecUseCase {


    private final ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase;
    private final BeneficiarioRepositoryPort beneficiarioRepositoryPort;

    public ConsultarYRegistrarReniecService (BeneficiarioRepositoryPort beneficiarioRepositoryPort, ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase) {
        this.beneficiarioRepositoryPort = beneficiarioRepositoryPort;
        this.consultarDatosPorDniUseCase = consultarDatosPorDniUseCase;
    }

    @Override
    public Beneficiario consultarYRegistrar(String dni) {
        DatosPersonales datosPersonales = consultarDatosPorDniUseCase.consultar(dni);

        return beneficiarioRepositoryPort.buscarPorDni(dni)
                .orElseGet(() -> beneficiarioRepositoryPort.guardar(
                        new Beneficiario(
                                0,
                                datosPersonales.getDni(),
                                datosPersonales.getNames(),
                                datosPersonales.getLastnames(),
                                Estado.ACTIVO
                        )
                ));
    }
}
