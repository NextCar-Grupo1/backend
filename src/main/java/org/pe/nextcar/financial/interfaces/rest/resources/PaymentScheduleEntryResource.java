package org.pe.nextcar.financial.interfaces.rest.resources;
import org.pe.nextcar.financial.domain.model.valueobjects.GracePeriodType;
import java.time.LocalDate;

public record PaymentScheduleEntryResource(
        int             periodNumber,
        LocalDate       paymentDate,
        double          initialBalance,
        double          amortization,
        double          interest,
        double          desgravamenInsurance,
        double          vehicleInsurance,
        double          portes,
        double          totalInstallment,
        double          finalBalance,
        GracePeriodType gracePeriodType,
        boolean         balloonPeriod
) {}
