package org.pe.nextcar.catalog.application.internal.commandservices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.catalog.domain.model.aggregates.Vehicle;
import org.pe.nextcar.catalog.domain.model.commands.CreateVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.DeleteVehicleCommand;
import org.pe.nextcar.catalog.domain.model.commands.UpdateVehicleCommand;
import org.pe.nextcar.catalog.domain.model.valueobjects.FuelType;
import org.pe.nextcar.catalog.domain.model.valueobjects.Transmission;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.pe.nextcar.catalog.infrastructure.persistence.jpa.repositories.VehicleRepository;
import org.pe.nextcar.financial.domain.model.valueobjects.LoanCurrency;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleCommandServiceImplTest {

    @Mock
    private VehicleRepository repository;

    @InjectMocks
    private VehicleCommandServiceImpl commandService;

    private CreateVehicleCommand validCreateCommand;

    @BeforeEach
    void setUp() {
        validCreateCommand = new CreateVehicleCommand(
                "Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "http://image.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "description");
    }

    @Test
    void handleCreate_ShouldSaveVehicle_WhenCommandIsValidAndNotDuplicate() {
        // Arrange
        when(repository.existsByBrandAndModelAndYear("Toyota", "Corolla", 2024)).thenReturn(false);
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = commandService.handle(validCreateCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Toyota", result.get().getBrand());
        assertEquals(89900.0, result.get().getPrice());
        verify(repository).save(any(Vehicle.class));
    }

    @Test
    void handleCreate_ShouldThrow_WhenVehicleAlreadyExists() {
        // Arrange
        when(repository.existsByBrandAndModelAndYear("Toyota", "Corolla", 2024)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commandService.handle(validCreateCommand));
        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(repository, never()).save(any());
    }

    @Test
    void handleCreate_ShouldThrow_WhenPriceIsZeroOrNegative() {
        // Arrange
        when(repository.existsByBrandAndModelAndYear(any(), any(), anyInt())).thenReturn(false);
        CreateVehicleCommand invalidPriceCommand = new CreateVehicleCommand(
                "Toyota", "Corolla", 2024, 0.0, LoanCurrency.SOLES,
                "http://image.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "description");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(invalidPriceCommand));
        verify(repository, never()).save(any());
    }

    @Test
    void handleUpdate_ShouldUpdateVehicle_WhenFoundAndValid() {
        // Arrange
        Vehicle existing = new Vehicle("Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "http://old.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "old description");
        UpdateVehicleCommand updateCommand = new UpdateVehicleCommand(
                1L, "Toyota", "Corolla", 2024, 95000.0, LoanCurrency.SOLES,
                "http://new.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "new description");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Vehicle> result = commandService.handle(updateCommand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(95000.0, result.get().getPrice());
        assertEquals("new description", result.get().getDescription());
        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(repository).save(captor.capture());
        assertEquals("http://new.png", captor.getValue().getImageUrl());
    }

    @Test
    void handleUpdate_ShouldThrow_WhenVehicleNotFound() {
        // Arrange
        UpdateVehicleCommand updateCommand = new UpdateVehicleCommand(
                99L, "Toyota", "Corolla", 2024, 95000.0, LoanCurrency.SOLES,
                "http://new.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "new description");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(updateCommand));
        verify(repository, never()).save(any());
    }

    @Test
    void handleUpdate_ShouldThrow_WhenPriceIsInvalid() {
        // Arrange
        Vehicle existing = new Vehicle("Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "http://old.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "old description");
        UpdateVehicleCommand updateCommand = new UpdateVehicleCommand(
                1L, "Toyota", "Corolla", 2024, -10.0, LoanCurrency.SOLES,
                "http://new.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "new description");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(updateCommand));
        verify(repository, never()).save(any());
    }

    @Test
    void handleDelete_ShouldMarkVehicleUnavailable_WhenFound() {
        // Arrange
        Vehicle existing = new Vehicle("Toyota", "Corolla", 2024, 89900.0, LoanCurrency.SOLES,
                "http://image.png", VehicleCategory.SEDAN, FuelType.GASOLINE,
                Transmission.CVT, 1800, 5, "description");
        assertTrue(existing.isAvailable());
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        commandService.handle(new DeleteVehicleCommand(1L));

        // Assert
        assertFalse(existing.isAvailable());
        verify(repository).save(existing);
    }

    @Test
    void handleDelete_ShouldThrow_WhenVehicleNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> commandService.handle(new DeleteVehicleCommand(99L)));
        verify(repository, never()).save(any());
    }
}
