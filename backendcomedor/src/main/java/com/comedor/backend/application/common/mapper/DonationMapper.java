package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Donation;
import com.comedor.backend.domain.model.DonationDetail;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationDetailResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DonationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DonationMapper {

    public DonationResponseDTO toResponse(Donation donation) {

        DonationResponseDTO dto = new DonationResponseDTO();

        dto.setId(donation.getId());
        dto.setDonationDate(donation.getDonationDate());
        dto.setStatus(donation.getStatus().name());

        dto.setDetails(
                donation.getDetails()
                        .stream()
                        .map(detail -> toDetailResponse(detail, donation.getId()))
                        .toList()
        );

        return dto;
    }

    private DonationDetailResponseDTO toDetailResponse(
            DonationDetail detail,
            Integer donationId
    ) {
        DonationDetailResponseDTO dto = new DonationDetailResponseDTO();

        dto.setDonationId(donationId);
        dto.setProductId(detail.getProduct().getId());
        dto.setDonationDetailId(detail.getId());
        dto.setProductName(detail.getProduct().getName());
        dto.setProductUnit(detail.getProduct().getUnit());
        dto.setQuantity(detail.getQuantity());

        return dto;
    }

}
