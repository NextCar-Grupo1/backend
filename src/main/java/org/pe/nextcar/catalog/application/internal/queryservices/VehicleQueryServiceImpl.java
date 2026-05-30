package org.pe.nextcar.catalog.application.internal.queryservices;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.queries.GetAllVehiclesQuery;
import org.pe.nextcar.catalog.domain.model.queries.GetVehicleByIdQuery;
import org.pe.nextcar.catalog.domain.services.VehicleQueryService;
import org.pe.nextcar.catalog.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VehicleQueryServiceImpl implements VehicleQueryService {

    private final VehicleRepository repository;

    public VehicleQueryServiceImpl(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Vehicle> handle(GetAllVehiclesQuery query) {
        if (query.category() != null) {
            return repository.findByCategoryAndAvailableTrueOrderByPriceAsc(query.category());
        }
        return repository.findByAvailableTrueOrderByBrandAscModelAsc();
    }

    @Override
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        return repository.findById(query.vehicleId());
    }
}