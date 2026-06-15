package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.MenuReportMapper;
import com.comedor.backend.application.ports.in.ListMenuReportDetailUseCase;
import com.comedor.backend.application.ports.in.ObtenerResumenReporteMenuUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.MenuReportRepositoryPort;
import com.comedor.backend.domain.model.BeneficiaryControl;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.StockMovement; // <-- Importación actualizada (antes Record)
import com.comedor.backend.domain.model.MenuReport;
import com.comedor.backend.domain.model.enums.MetodoPago;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DetalleReporteMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ListMenuReportDetailResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ResumenReporteMenuResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class ListMenuReportDetailService implements ListMenuReportDetailUseCase {

    private final MenuReportRepositoryPort menuReportRepositoryPort;
    private final MenuReportMapper menuReportMapper;
    private final PersonRepositoryPort personRepositoryPort;
    private final ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase;

    public ListMenuReportDetailService(MenuReportRepositoryPort menuReportRepositoryPort,
                                       MenuReportMapper menuReportMapper,
                                       PersonRepositoryPort personRepositoryPort,
                                       ObtenerResumenReporteMenuUseCase obtenerResumenReporteMenuUseCase) {
        this.menuReportRepositoryPort = menuReportRepositoryPort;
        this.menuReportMapper = menuReportMapper;
        this.personRepositoryPort = personRepositoryPort;
        this.obtenerResumenReporteMenuUseCase = obtenerResumenReporteMenuUseCase;
    }


    @Override
    public ListMenuReportDetailResponseDTO list(
            LocalDate startDate,
            LocalDate endDate
    ) {

        List<MenuReport> reports =
                menuReportRepositoryPort.showMenuReport(
                        startDate,
                        endDate
                );

        List<DetalleReporteMenuResponseDTO> details =
                reports.stream()
                        .map(reporte -> {

                            String menu =
                                    reporte.getDishMenu() != null
                                            ? reporte.getDishMenu().getName()
                                            : "";

                            List<Person> cocineras =
                                    personRepositoryPort.findAllByIds(
                                            reporte.getCooks()
                                    );

                            ResumenReporteMenuResponseDTO resumen =
                                    obtenerResumenReporteMenuUseCase
                                            .obtenerResumen(
                                                    reporte.getId()
                                            );

                            return menuReportMapper.toDetalleDto(
                                    reporte,
                                    menu,
                                    cocineras,
                                    reporte.getStockMovements(),
                                    reporte.getBeneficiaryControls(),
                                    resumen
                            );

                        })
                        .toList();

        BigDecimal totalEarned =
                details.stream()
                        .map(d -> d.getResumenReporteMenu().getTotalEarned())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent =
                details.stream()
                        .map(d -> d.getResumenReporteMenu().getTotalSpent())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal net =
                totalEarned.subtract(totalSpent);

        int uniqueBeneficiaryCount =
                details.stream()
                        .flatMap(d -> d.getBeneficiarios().stream())
                        .map(b -> b.getId())
                        .collect(Collectors.toSet())
                        .size();

        MetodoPago mostUsed =
                details.stream()
                        .map(d -> d.getResumenReporteMenu().getMostUsedPaymentMethod())
                        .filter(Objects::nonNull)
                        .collect(groupingBy(identity(), counting()))
                        .entrySet()
                        .stream()
                        .max(comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);

        return menuReportMapper.listMenuReportDetailResponseDTO(
                details,
                totalEarned,
                totalSpent,
                net,
                uniqueBeneficiaryCount,
                mostUsed
        );
    }
}