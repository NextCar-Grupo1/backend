package org.pe.nextcar.financial.interfaces.rest.resources;

public record FinancialEntityResource(
        String name,
        String displayName,
        double smartPurchaseResidualRate,
        double defaultDesgravamenRate,
        double defaultVehicleInsurance,
        double defaultPortes,
        int    maxGracePeriodMonths
) {}
