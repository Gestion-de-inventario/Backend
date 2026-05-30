package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ConsultarDatosPorDniUseCase;
import com.comedor.backend.application.ports.in.ConsultarYRegistrarReniecUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Estado;

public class ConsultarYRegistrarReniecService implements ConsultarYRegistrarReniecUseCase {


    private final ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase;
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    public ConsultarYRegistrarReniecService (BeneficiaryRepositoryPort beneficiaryRepositoryPort, ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.consultarDatosPorDniUseCase = consultarDatosPorDniUseCase;
    }

    @Override
    public Beneficiary consultarYRegistrar(String dni) {
        PersonalDataReniec personalDataReniec = consultarDatosPorDniUseCase.consultar(dni);

        return beneficiaryRepositoryPort.buscarPorDni(dni)
                .orElseGet(() -> beneficiaryRepositoryPort.guardar(
                        new Beneficiary(
                                0,
                                personalDataReniec.getDni(),
                                personalDataReniec.getNames(),
                                personalDataReniec.getLastnames(),
                                Estado.ACTIVO
                        )
                ));
    }
}
