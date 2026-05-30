package org.pe.nextcar.catalog.interfaces.rest.resources;

import org.pe.nextcar.catalog.domain.model.valueobjects.*;
import org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency;

/**
 * Response DTO para el catálogo de vehículos.
 * estimatedMonthlyPayment: cuota referencial (TEA 12.5%, 20% inicial, 36 meses)
 * para mostrar en la card del frontend sin que el usuario tenga que simular.
 */
public record VehicleResource(
        Long             id,
        String           brand,
        String           model,
        int              year,
        double           price,
        LoanCurrency     currency,
        String           imageUrl,
        VehicleCategory  category,
        String           categoryDisplayName,
        FuelType         fuelType,
        String           fuelTypeDisplayName,
        Transmission     transmission,
        String           transmissionDisplayName,
        int              engineCC,
        int              seatingCapacity,
        String           description,
        double           estimatedMonthlyPayment
) {}