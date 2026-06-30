package org.pe.nextcar.catalog.domain.services;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.commands.CreateVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.DeleteVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.UpdateVehicleCommand;

import java.util.Optional;

public interface VehicleCommandService {
    Optional<Vehicle> handle(CreateVehicleCommand command);
    Optional<Vehicle> handle(UpdateVehicleCommand command);
    void               handle(DeleteVehicleCommand command);
}
