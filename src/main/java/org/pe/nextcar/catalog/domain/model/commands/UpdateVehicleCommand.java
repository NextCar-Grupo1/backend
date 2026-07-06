package org.pe.nextcar.catalog.domain.model.commands;

import org.pe.nextcar.catalog.domain.model.valueobjects.FuelType;
import org.pe.nextcar.catalog.domain.model.valueobjects.Transmission;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency;

public record UpdateVehicleCommand(
        Long            vehicleId,
        String          brand,
        String          model,
        int             year,
        double          price,
        LoanCurrency    currency,
        String          imageUrl,
        VehicleCategory category,
        FuelType        fuelType,
        Transmission    transmission,
        int             engineCC,
        int             seatingCapacity,
        String          description
) {}
