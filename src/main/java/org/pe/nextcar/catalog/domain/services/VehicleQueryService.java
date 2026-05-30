package org.pe.nextcar.catalog.domain.services;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.queries.GetAllVehiclesQuery;
import org.pe.nextcar.catalog.domain.model.queries.GetVehicleByIdQuery;
import java.util.List;
import java.util.Optional;

public interface VehicleQueryService {
    List<Vehicle>     handle(GetAllVehiclesQuery query);
    Optional<Vehicle> handle(GetVehicleByIdQuery query);
}