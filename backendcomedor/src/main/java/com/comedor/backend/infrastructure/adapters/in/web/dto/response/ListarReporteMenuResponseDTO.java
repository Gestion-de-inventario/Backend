package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import java.time.LocalDate;
import java.util.List;

public class ListarReporteMenuResponseDTO {
    private int id;
    private LocalDate date;
    private String day;
    private List<PersonaResponseDTO> cocineras;
    private List<RegistroProductoResponseDTO> registro;
    private List <RegistroBeneficiarioResponseDTO> beneficiarios;
    private ResumenReporteMenuResponseDTO resumenReporteMenu;
}
