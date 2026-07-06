package org.pe.nextcar.catalog.application.internal.queryservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.queries.GetAllVehiclesQuery;
import org.pe.nextcar.catalog.domain.model.queries.GetVehicleByIdQuery;
import org.pe.nextcar.catalog.domain.model.valueobjects.FuelType;
import org.pe.nextcar.catalog.domain.model.valueobjects.Transmission;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.pe.nextcar.catalog.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleQueryServiceImplTest {

    @Mock
    private VehicleRepository repository;

    @InjectMocks
    private VehicleQueryServiceImpl queryService;

    @Test
    void handleGetAll_ShouldReturnAllAvailableVehicles_WhenCategoryIsNull() {
        // Arrange
        Vehicle vehicle = new Vehicle("Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "img", VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT, 1800, 5, "d");
        when(repository.findByAvailableTrueOrderByBrandAscModelAsc()).thenReturn(List.of(vehicle));

        // Act
        List<Vehicle> result = queryService.handle(new GetAllVehiclesQuery(null));

        // Assert
        assertEquals(1, result.size());
        verify(repository).findByAvailableTrueOrderByBrandAscModelAsc();
        verify(repository, never()).findByCategoryAndAvailableTrueOrderByPriceAsc(any());
    }

    @Test
    void handleGetAll_ShouldFilterByCategory_WhenCategoryProvided() {
        // Arrange
        Vehicle vehicle = new Vehicle("Hyundai", "Tucson", 2024, 125900.0, LoanCurrency.SOLES,
                "img", VehicleCategory.SUV, FuelType.GASOLINE, Transmission.AUTOMATIC, 2000, 5, "d");
        when(repository.findByCategoryAndAvailableTrueOrderByPriceAsc(VehicleCategory.SUV))
                .thenReturn(List.of(vehicle));

        // Act
        List<Vehicle> result = queryService.handle(new GetAllVehiclesQuery(VehicleCategory.SUV));

        // Assert
        assertEquals(1, result.size());
        assertEquals("Hyundai", result.get(0).getBrand());
        verify(repository).findByCategoryAndAvailableTrueOrderByPriceAsc(VehicleCategory.SUV);
        verify(repository, never()).findByAvailableTrueOrderByBrandAscModelAsc();
    }

    @Test
    void handleGetById_ShouldReturnVehicle_WhenExists() {
        // Arrange
        Vehicle vehicle = new Vehicle("Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "img", VehicleCategory.SEDAN, FuelType.GASOLINE, Transmission.CVT, 1800, 5, "d");
        when(repository.findById(1L)).thenReturn(Optional.of(vehicle));

        // Act
        Optional<Vehicle> result = queryService.handle(new GetVehicleByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Corolla", result.get().getModel());
    }

    @Test
    void handleGetById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Vehicle> result = queryService.handle(new GetVehicleByIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
    }
}
