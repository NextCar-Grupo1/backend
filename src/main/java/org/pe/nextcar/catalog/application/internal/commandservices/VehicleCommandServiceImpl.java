package org.pe.nextcar.catalog.application.internal.commandservices;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.commands.CreateVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.DeleteVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.UpdateVehicleCommand;
import org.pe.nextcar.catalog.domain.services.VehicleCommandService;
import org.pe.nextcar.catalog.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository repository;

    public VehicleCommandServiceImpl(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Vehicle> handle(CreateVehicleCommand cmd) {
        if (repository.existsByBrandAndModelAndYear(cmd.brand(), cmd.model(), cmd.year())) {
            throw new IllegalArgumentException(
                    "Ya existe un vehículo " + cmd.brand() + " " + cmd.model() + " " + cmd.year()
                            + " en el catálogo.");
        }
        if (cmd.price() <= 0) {
            throw new IllegalArgumentException("El precio del vehículo debe ser mayor a 0.");
        }

        var vehicle = new Vehicle(
                cmd.brand(), cmd.model(), cmd.year(), cmd.price(), cmd.currency(),
                cmd.imageUrl(), cmd.category(), cmd.fuelType(), cmd.transmission(),
                cmd.engineCC(), cmd.seatingCapacity(), cmd.description()
        );
        return Optional.of(repository.save(vehicle));
    }

    @Override
    public Optional<Vehicle> handle(UpdateVehicleCommand cmd) {
        var vehicle = repository.findById(cmd.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehículo con id " + cmd.vehicleId() + " no encontrado."));

        if (cmd.price() <= 0) {
            throw new IllegalArgumentException("El precio del vehículo debe ser mayor a 0.");
        }

        vehicle.updateDetails(
                cmd.brand(), cmd.model(), cmd.year(), cmd.price(), cmd.currency(),
                cmd.imageUrl(), cmd.category(), cmd.fuelType(), cmd.transmission(),
                cmd.engineCC(), cmd.seatingCapacity(), cmd.description()
        );
        return Optional.of(repository.save(vehicle));
    }

    @Override
    public void handle(DeleteVehicleCommand cmd) {
        var vehicle = repository.findById(cmd.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehículo con id " + cmd.vehicleId() + " no encontrado."));
        vehicle.markUnavailable();
        repository.save(vehicle);
    }
}
