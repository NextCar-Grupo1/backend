package org.pe.nextcar.financial.interfaces.rest.resources;

import org.pe.nextcar.financial.domain.model.valueobjects.*;
import java.time.LocalDate;
import java.util.List;

/** Full simulation response — matches the "Resultados de Simulación" screen. */
public record LoanSimulationResource(
        Long   id,

        // Input summary
        LoanCurrency    currency,
        double          vehiclePrice,
        double          initialFee,
        double          principal,
        int             termMonths,
        LocalDate       startDate,
        PaymentMethod   paymentMethod,
        RateType        rateType,
        double          rateValue,
        CapitalizationFrequency capitalizationFrequency,
        GracePeriodType gracePeriodType,
        int             gracePeriodMonths,
        String          financialEntityName,

        // Computed indicators
        double          monthlyEffectiveRate,
        double          baseInstallment,
        double          totalMonthlyInstallment,
        double          npv,
        double          monthlyIrr,
        double          tcea,

        // Totals
        double          totalInterestPaid,
        double          totalInsurancePaid,
        double          totalAmortization,
        double          totalPaid,

        // Schedule
        List<PaymentScheduleEntryResource> schedule
) {}