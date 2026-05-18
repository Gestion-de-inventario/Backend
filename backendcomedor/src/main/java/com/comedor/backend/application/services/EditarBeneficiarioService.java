package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.DniYaRegistradoException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class EditarBeneficiarioService implements EditarBeneficiarioUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public EditarBeneficiarioService (BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public Beneficiary editar(int id, EditarBeneficiarioRequestDTO editarBeneficiarioRequest) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.findById(id)
                .orElseThrow(() -> new BeneficiarioNoEncontradoException("Usuario No Encontrado: " + id));

        if (editarBeneficiarioRequest.getDni() != null && !beneficiary.getDni().equals(editarBeneficiarioRequest.getDni())) {
            if (beneficiaryRepositoryPort.existePorDni(editarBeneficiarioRequest.getDni())) {
                throw new DniYaRegistradoException("Ya existe un Beneficiario con el DNI: " + editarBeneficiarioRequest.getDni());
            }
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "dni", beneficiary.getDni(), editarBeneficiarioRequest.getDni()
            ));
            beneficiary.setDni(editarBeneficiarioRequest.getDni()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getName() != null && !editarBeneficiarioRequest.getName().equals(beneficiary.getName())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "name", beneficiary.getName(), editarBeneficiarioRequest.getName()
            ));
            beneficiary.setName(editarBeneficiarioRequest.getName()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getLastname() != null && !editarBeneficiarioRequest.getLastname().equals(beneficiary.getLastname())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "lastname", beneficiary.getLastname(), editarBeneficiarioRequest.getLastname()
            ));
            beneficiary.setLastname(editarBeneficiarioRequest.getLastname()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getStatus() != null && !editarBeneficiarioRequest.getStatus().equals(beneficiary.getStatus())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "status",
                    beneficiary.getStatus().toString(),
                    editarBeneficiarioRequest.getStatus().toString()
            ));
            beneficiary.setStatus(editarBeneficiarioRequest.getStatus()); // ✅ Agregar
        }

        return beneficiaryRepositoryPort.guardar(beneficiary);
    }
}
