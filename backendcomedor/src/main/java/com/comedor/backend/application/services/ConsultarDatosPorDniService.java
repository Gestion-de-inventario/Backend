package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ConsultarDatosPorDniUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.ReniecPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.PersonalDataReniec;

import java.util.Optional;

public class ConsultarDatosPorDniService implements ConsultarDatosPorDniUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final ReniecPort reniecPort;

    public ConsultarDatosPorDniService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, ReniecPort reniecPort){
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
}
