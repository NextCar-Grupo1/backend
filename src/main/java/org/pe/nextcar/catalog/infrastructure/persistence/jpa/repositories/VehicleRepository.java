package org.pe.nextcar.catalog.infrastructure.persistence.jpa.repositories;

import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByAvailableTrueOrderByBrandAscModelAsc();
    List<Vehicle> findByCategoryAndAvailableTrueOrderByPriceAsc(VehicleCategory category);
    boolean existsByBrandAndModelAndYear(String brand, String model, int year);
}