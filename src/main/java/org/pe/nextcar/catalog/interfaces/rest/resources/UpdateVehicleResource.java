package org.pe.nextcar.catalog.interfaces.rest.resources;

import jakarta.validation.constraints.*;
import org.pe.nextcar.catalog.domain.model.valueobjects.FuelType;
import org.pe.nextcar.catalog.domain.model.valueobjects.Transmission;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency;

public record UpdateVehicleResource(
        @NotBlank String          brand,
        @NotBlank String          model,
        @Min(1980) @Max(2100) int year,
        @Positive double          price,
        @NotNull  LoanCurrency    currency,
        @NotBlank String          imageUrl,
        @NotNull  VehicleCategory category,
        @NotNull  FuelType        fuelType,
        @NotNull  Transmission    transmission,
        @Positive int             engineCC,
        @Positive int             seatingCapacity,
        @NotBlank String          description
) {}
