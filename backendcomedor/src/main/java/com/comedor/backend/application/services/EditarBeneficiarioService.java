package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarBeneficiarioUseCase;
import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.DniYaRegistradoException;
import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;

public class EditarBeneficiarioService implements EditarBeneficiarioUseCase {

    private final BeneficiarioRepositoryPort beneficiarioRepositoryPort;

    public EditarBeneficiarioService (BeneficiarioRepositoryPort beneficiarioRepositoryPort) {
        this.beneficiarioRepositoryPort = beneficiarioRepositoryPort;
    }

    @Override
    public Beneficiario editar(int id, EditarBeneficiarioRequestDTO editarBeneficiarioRequest) {
        Beneficiario beneficiario = beneficiarioRepositoryPort.findById(id)
                .orElseThrow(() -> new BeneficiarioNoEncontradoException("Usuario No Entontrad" + id));

        if(!beneficiario.getDni().equals(editarBeneficiarioRequest.getDni())) {
            boolean dniExiste = beneficiarioRepositoryPort.existePorDni(editarBeneficiarioRequest.getDni());
            if (dniExiste) {
                throw new DniYaRegistradoException("Ya existe un Beneficiario con el DNI: "+ editarBeneficiarioRequest.getDni());
            }
        }

        Beneficiario beneficiarioActualizado = beneficiario.actualizar(
                editarBeneficiarioRequest.getDni() != null ? editarBeneficiarioRequest.getDni() : beneficiario.getDni(),
                editarBeneficiarioRequest.getName() !=null ? editarBeneficiarioRequest.getName() : beneficiario.getName(),
                editarBeneficiarioRequest.getLastName() != null ? editarBeneficiarioRequest.getLastName() : beneficiario.getLastname(),
                editarBeneficiarioRequest.getStatus() != null ? editarBeneficiarioRequest.getStatus() : beneficiario.getStatus()
        );

        return beneficiarioRepositoryPort.guardar(beneficiarioActualizado);
    }

}
