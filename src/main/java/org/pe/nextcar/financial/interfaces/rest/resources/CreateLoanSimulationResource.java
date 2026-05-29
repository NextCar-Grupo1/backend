package org.pe.nextcar.financial.interfaces.rest.resources;

import jakarta.validation.constraints.*;
import org.pe.nextcar.financial.domain.model.valueobjects.*;
import java.time.LocalDate;

public record CreateLoanSimulationResource(
        @NotNull LoanCurrency    currency,
        @Positive double         vehiclePrice,
        @DecimalMin("0.20") @DecimalMax("0.80") double initialFeeRate,
        @Min(6) @Max(84) int     termMonths,
        @NotNull LocalDate       startDate,
        @NotNull PaymentMethod   paymentMethod,
        @NotNull RateType        rateType,
        @Positive double         rateValue,
        @NotNull CapitalizationFrequency capitalizationFrequency,
        @NotNull GracePeriodType gracePeriodType,
        @Min(0) int              gracePeriodMonths,
        @NotNull FinancialEntity financialEntity,
        @Positive double         desgravamenRate,
        @PositiveOrZero double   vehicleInsuranceMonthly,
        @PositiveOrZero double   portesMonthly
) {}
