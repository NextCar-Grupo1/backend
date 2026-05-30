package org.pe.nextcar.catalog.domain.model.queries;

import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
public record GetAllVehiclesQuery(VehicleCategory category) {
    public GetAllVehiclesQuery() { this(null); }
}