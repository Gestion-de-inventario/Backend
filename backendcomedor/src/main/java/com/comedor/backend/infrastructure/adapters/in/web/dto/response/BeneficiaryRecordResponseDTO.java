package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class BeneficiaryRecordResponseDTO {
    private int id;
    private String name;
    private String lastName;
    private int cantidad;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private boolean pago;
    private boolean entregado;
}
