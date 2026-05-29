package org.pe.nextcar.financial.domain.model.commands;

import org.pe.nextcar.financial.domain.model.valueobjects.*;
import java.time.LocalDate;

public record CreateLoanSimulationCommand(
        Long            userId,
        LoanCurrency    currency,
        double          vehiclePrice,
        double          initialFeeRate,
        int             termMonths,
        LocalDate       startDate,
        PaymentMethod   paymentMethod,
        RateType        rateType,
        double          rateValue,
        CapitalizationFrequency capitalizationFrequency,
        GracePeriodType gracePeriodType,
        int             gracePeriodMonths,
        FinancialEntity financialEntity,
        double          desgravamenRate,
        double          vehicleInsuranceMonthly,
        double          portesMonthly
) {}
