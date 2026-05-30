package org.pe.nextcar.catalog.interfaces.rest.transform;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.interfaces.rest.resources.VehicleResource;

public class VehicleAssembler {
    public static VehicleResource toResource(Vehicle v) {
        return new VehicleResource(
                v.getId(), v.getBrand(), v.getModel(), v.getYear(),
                v.getPrice(), v.getCurrency(), v.getImageUrl(),
                v.getCategory(), v.getCategory().getDisplayName(),
                v.getFuelType(), v.getFuelType().getDisplayName(),
                v.getTransmission(), v.getTransmission().getDisplayName(),
                v.getEngineCC(), v.getSeatingCapacity(), v.getDescription(),
                v.getEstimatedMonthlyPayment()
        );
    }
}