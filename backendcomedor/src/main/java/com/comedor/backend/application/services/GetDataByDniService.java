package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetDataByDniUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.ReniecPort;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.PersonalDataReniec;

import java.util.Optional;

public class GetDataByDniService implements GetDataByDniUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final ReniecPort reniecPort;

    public GetDataByDniService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ReniecPort reniecPort){
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.reniecPort = reniecPort;

    }

    @Override
    public PersonalDataReniec consultar(String dni) {
        Optional<Beneficiary> beneficiarioLocal = beneficiaryRepositoryPort.buscarPorDni(dni);

        if(beneficiarioLocal.isPresent()){
            Beneficiary b = beneficiarioLocal.get();

            return new PersonalDataReniec(b.getDni(),b.getName(),b.getLastname());
        }

        Optional<PersonalDataReniec> datosReniec = reniecPort.consultarPorDni(dni);

        if(datosReniec.isPresent()){
            return datosReniec.get();
        }

        throw new IllegalArgumentException("El DNI ingresado no existe en los registros");
    }

    @Override
    public Beneficiary consultarBeneficiary(String dni) {
        Optional<Beneficiary> beneficiary= beneficiaryRepositoryPort.buscarPorDni(dni);

        return beneficiary.orElseThrow(() -> new BeneficiaryNotFoundException("Beneficiario no encontrado"));
    }
}
