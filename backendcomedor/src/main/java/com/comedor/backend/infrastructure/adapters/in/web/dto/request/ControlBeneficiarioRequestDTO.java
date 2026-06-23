package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import com.comedor.backend.domain.model.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ControlBeneficiarioRequestDTO {
    private int beneficiarioId;//
    private Boolean pago;
    private Boolean entregado;
    private PaymentMethod payMethod;
    private Integer menusAmount;//
    private BigDecimal menuPrice;//
}