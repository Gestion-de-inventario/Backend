package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Donation;
import com.comedor.backend.domain.model.enums.StatusOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface DonationRepositoryPort {
    Donation save(Donation donation);
    Page<Donation> showDonation(
            LocalDate startDate,
            LocalDate endDate,
            StatusOrder status,
            Pageable pageable
    );
    Donation findById(Integer id);
    Donation changeStatus(Integer id, StatusOrder status);
}
