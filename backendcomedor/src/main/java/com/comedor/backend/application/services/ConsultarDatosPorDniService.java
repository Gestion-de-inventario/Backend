package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ConsultarDatosPorDniUseCase;
import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.application.ports.out.ReniecPort;
import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.domain.model.DatosPersonales;

import java.util.Optional;

public class ConsultarDatosPorDniService implements ConsultarDatosPorDniUseCase {

    private final BeneficiarioRepositoryPort beneficiarioRepositoryPort;
    private final ReniecPort reniecPort;

    public ConsultarDatosPorDniService(BeneficiarioRepositoryPort beneficiarioRepositoryPort, ReniecPort reniecPort){
        this.beneficiarioRepositoryPort = beneficiarioRepositoryPort;
        this.reniecPort = reniecPort;

    }

    @Override
    public DatosPersonales consultar(String dni) {
        Optional<Beneficiario> beneficiarioLocal = beneficiarioRepositoryPort.buscarPorDni(dni);

        if(beneficiarioLocal.isPresent()){
            Beneficiario b = beneficiarioLocal.get();

            return new DatosPersonales(b.getDni(),b.getName(),b.getLastname());
        }

        Optional<DatosPersonales> datosReniec = reniecPort.consultarPorDni(dni);

        if(datosReniec.isPresent()){
            return datosReniec.get();
        }

        throw new IllegalArgumentException("El DNI ingresado no existe en los registros");
    }
}
