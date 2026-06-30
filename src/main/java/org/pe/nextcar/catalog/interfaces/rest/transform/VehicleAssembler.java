package org.pe.nextcar.catalog.interfaces.rest.transform;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.commands.CreateVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.UpdateVehicleCommand;
import org.pe.nextcar.catalog.interfaces.rest.resources.CreateVehicleResource;
import org.pe.nextcar.catalog.interfaces.rest.resources.UpdateVehicleResource;
import org.pe.nextcar.catalog.interfaces.rest.resources.VehicleResource;

public class VehicleAssembler {

    public static CreateVehicleCommand toCommand(CreateVehicleResource r) {
        return new CreateVehicleCommand(
            r.brand(), r.model(), r.year(), r.price(), r.currency(),
            r.imageUrl(), r.category(), r.fuelType(), r.transmission(),
            r.engineCC(), r.seatingCapacity(), r.description()
        );
    }

    public static UpdateVehicleCommand toCommand(Long vehicleId, UpdateVehicleResource r) {
        return new UpdateVehicleCommand(
            vehicleId, r.brand(), r.model(), r.year(), r.price(), r.currency(),
            r.imageUrl(), r.category(), r.fuelType(), r.transmission(),
            r.engineCC(), r.seatingCapacity(), r.description()
        );
    }

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