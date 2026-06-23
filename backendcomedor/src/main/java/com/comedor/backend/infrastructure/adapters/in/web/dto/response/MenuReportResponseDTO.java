package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.StatusMenuReport;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MenuReportResponseDTO {
    private Integer id;
    private LocalDate date;
    private String day;
    private Integer dishId;
    private String dishName;
    private List<StockMovementResponseDTO> registers;
    private List<BeneficiaryRecordResponseDTO> beneficiaries;
    private List<Integer> cooks;
    private Integer quantityPrepared;
    private Integer quantityRemaining;
    private StatusMenuReport status;
}
