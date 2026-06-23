package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ConsultarDatosPorDniUseCase;
import com.comedor.backend.application.ports.in.ConsultarYRegistrarReniecUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Estado;

public class ConsultarYRegistrarReniecService implements ConsultarYRegistrarReniecUseCase {


    private final ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase;
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    public ConsultarYRegistrarReniecService (BeneficiaryRepositoryPort beneficiaryRepositoryPort, ConsultarDatosPorDniUseCase consultarDatosPorDniUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.consultarDatosPorDniUseCase = consultarDatosPorDniUseCase;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
    }

    @Override
    public Beneficiary consultarYRegistrar(String dni) {
        PersonalDataReniec personalDataReniec = consultarDatosPorDniUseCase.consultar(dni);
        BeneficiaryType defaulType = beneficiaryTypeRepositoryPort.findById(1);
        return beneficiaryRepositoryPort.buscarPorDni(dni)
                .orElseGet(() -> beneficiaryRepositoryPort.guardar(
                        new Beneficiary(
                                0,
                                personalDataReniec.getDni(),
                                personalDataReniec.getNames(),
                                personalDataReniec.getLastnames(),
                                Estado.ACTIVO,
                                defaulType

                        )
                ));
    }
}
