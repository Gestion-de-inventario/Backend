package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Status;

import java.util.List;

public interface BeneficiaryTypeRepositoryPort {

    BeneficiaryType save(BeneficiaryType beneficiaryType);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name,Integer id);
    BeneficiaryType findById(Integer id);
    List<BeneficiaryType> findByStatus(Status status);
    BeneficiaryType update(BeneficiaryType beneficiaryType);

}
