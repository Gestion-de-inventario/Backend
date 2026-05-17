package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarBeneficiarioUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.DniYaRegistradoException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;

public class EditarBeneficiarioService implements EditarBeneficiarioUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    public EditarBeneficiarioService (BeneficiaryRepositoryPort beneficiaryRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
    }

    @Override
    public Beneficiary editar(int id, EditarBeneficiarioRequestDTO editarBeneficiarioRequest) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.findById(id)
                .orElseThrow(() -> new BeneficiarioNoEncontradoException("Usuario No Entontrad" + id));

        if(!beneficiary.getDni().equals(editarBeneficiarioRequest.getDni())) {
            boolean dniExiste = beneficiaryRepositoryPort.existePorDni(editarBeneficiarioRequest.getDni());
            if (dniExiste) {
                throw new DniYaRegistradoException("Ya existe un Beneficiario con el DNI: "+ editarBeneficiarioRequest.getDni());
            }
        }

        Beneficiary beneficiaryUpdated = beneficiary.update(
                editarBeneficiarioRequest.getDni() != null ? editarBeneficiarioRequest.getDni() : beneficiary.getDni(),
                editarBeneficiarioRequest.getName() !=null ? editarBeneficiarioRequest.getName() : beneficiary.getName(),
                editarBeneficiarioRequest.getLastName() != null ? editarBeneficiarioRequest.getLastName() : beneficiary.getLastname(),
                editarBeneficiarioRequest.getStatus() != null ? editarBeneficiarioRequest.getStatus() : beneficiary.getStatus()
        );

        return beneficiaryRepositoryPort.guardar(beneficiaryUpdated);
    }

}
