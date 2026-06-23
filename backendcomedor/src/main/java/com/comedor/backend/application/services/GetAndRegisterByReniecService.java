package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetDataByDniUseCase;
import com.comedor.backend.application.ports.in.GetAndRegisterByReniecUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.Status;

public class GetAndRegisterByReniecService implements GetAndRegisterByReniecUseCase {


    private final GetDataByDniUseCase getDataByDniUseCase;
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    public GetAndRegisterByReniecService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, GetDataByDniUseCase getDataByDniUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.getDataByDniUseCase = getDataByDniUseCase;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
    }

    @Override
    public Beneficiary consultarYRegistrar(String dni) {
        PersonalDataReniec personalDataReniec = getDataByDniUseCase.consultar(dni);
        BeneficiaryType defaulType = beneficiaryTypeRepositoryPort.findById(1);
        return beneficiaryRepositoryPort.buscarPorDni(dni)
                .orElseGet(() -> beneficiaryRepositoryPort.guardar(
                        new Beneficiary(
                                0,
                                personalDataReniec.getDni(),
                                personalDataReniec.getNames(),
                                personalDataReniec.getLastnames(),
                                Status.ACTIVO,
                                defaulType

                        )
                ));
    }
}
