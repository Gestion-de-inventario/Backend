package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.PersonalDataReniec;

public interface GetDataByDniUseCase {
    PersonalDataReniec consultar(String dni);
    Beneficiary consultarBeneficiary(String dni);
}
