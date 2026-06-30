package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetDataByDniUseCase;
import com.comedor.backend.application.ports.in.GetAndRegisterByReniecUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryAlreadyRegisteredException;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
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
        if (beneficiaryRepositoryPort.existePorDni(dni)) {
            throw new BeneficiaryAlreadyRegisteredException(
                    "Ya existe un beneficiario registrado con el DNI " + dni
            );
        }

        PersonalDataReniec personalDataReniec =
                getDataByDniUseCase.consultar(dni);

        BeneficiaryType defaultType =
                beneficiaryTypeRepositoryPort.findById(1);

        Beneficiary beneficiary = new Beneficiary(
                0,
                personalDataReniec.getDni(),
                personalDataReniec.getNames(),
                personalDataReniec.getLastnames(),
                Status.ACTIVO,
                defaultType
        );

        return beneficiaryRepositoryPort.guardar(beneficiary);
    }
}
