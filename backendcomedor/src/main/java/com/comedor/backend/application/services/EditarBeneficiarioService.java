package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditarBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiarioNoEncontradoException;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInactiveException;
import com.comedor.backend.domain.exceptions.DniYaRegistradoException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class EditarBeneficiarioService implements EditarBeneficiarioUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    public EditarBeneficiarioService (BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
    }

    @Override
    public Beneficiary editar(int id, EditarBeneficiarioRequestDTO editarBeneficiarioRequest) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.findById(id)
                .orElseThrow(() -> new BeneficiarioNoEncontradoException("Usuario No Encontrado: " + id));

        BeneficiaryType newBeneciaryType = beneficiaryTypeRepositoryPort.findById(editarBeneficiarioRequest.getBeneficiaryTypeId());

        if (editarBeneficiarioRequest.getDni() != null && !beneficiary.getDni().equals(editarBeneficiarioRequest.getDni())) {
            if (beneficiaryRepositoryPort.existePorDni(editarBeneficiarioRequest.getDni())) {
                throw new DniYaRegistradoException("Ya existe un Beneficiario con el DNI: " + editarBeneficiarioRequest.getDni());
            }
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "dni", beneficiary.getDni(), editarBeneficiarioRequest.getDni()
            ));
            beneficiary.setDni(editarBeneficiarioRequest.getDni()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getName() != null && !editarBeneficiarioRequest.getName().equalsIgnoreCase(beneficiary.getName())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "name", beneficiary.getName(), editarBeneficiarioRequest.getName()
            ));
            beneficiary.setName(editarBeneficiarioRequest.getName()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getLastname() != null && !editarBeneficiarioRequest.getLastname().equalsIgnoreCase(beneficiary.getLastname())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "lastname", beneficiary.getLastname(), editarBeneficiarioRequest.getLastname()
            ));
            beneficiary.setLastname(editarBeneficiarioRequest.getLastname()); // ✅ Agregar
        }

        if(editarBeneficiarioRequest.getBeneficiaryTypeId() != null && !editarBeneficiarioRequest.getBeneficiaryTypeId().equals(beneficiary.getBeneficiaryType().getId()))
        {

            if(newBeneciaryType.getStatus().equals(Estado.INACTIVO))
            {
                throw new BeneficiaryTypeInactiveException("No puedes elegir un tipo inactivo. El tipo de beneficiario : "+ newBeneciaryType.getName()+" ,esta inactivo.");
            }

            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "Tipo de beneficiario",
                    beneficiary.getBeneficiaryType().getName(),
                    newBeneciaryType.getName()
            ));
            beneficiary.setBeneficiaryType(newBeneciaryType);
        }

        return beneficiaryRepositoryPort.guardar(beneficiary);
    }
}
