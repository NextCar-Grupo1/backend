package org.pe.nextcar.financial.application.internal.queryservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationByIdQuery;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationsByUserIdQuery;
import org.pe.nextcar.financial.domain.model.valueobjects.*;
import org.pe.nextcar.financial.infrastructure.persistence.jpa.repositories.LoanSimulationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanSimulationQueryServiceImplTest {

    @Mock
    private LoanSimulationRepository repository;

    @InjectMocks
    private LoanSimulationQueryServiceImpl queryService;

    private LoanSimulation sampleSimulation() {
        return new LoanSimulation(1L, LoanCurrency.SOLES, 89900.0, 0.20, 36,
                LocalDate.of(2026, 1, 15), PaymentMethod.FRENCH, RateType.TEA, 0.125,
                CapitalizationFrequency.MONTHLY, GracePeriodType.NONE, 0,
                FinancialEntity.BCP, 0.0005, 150.0, 10.0);
    }

    @Test
    void handleGetById_ShouldReturnSimulation_WhenExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(sampleSimulation()));

        // Act
        Optional<LoanSimulation> result = queryService.handle(new GetLoanSimulationByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(89900.0, result.get().getVehiclePrice());
    }

    @Test
    void handleGetById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<LoanSimulation> result = queryService.handle(new GetLoanSimulationByIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void handleGetByUserId_ShouldReturnSimulationsOrderedByCreatedAtDesc() {
        // Arrange
        when(repository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(sampleSimulation()));

        // Act
        List<LoanSimulation> result = queryService.handle(new GetLoanSimulationsByUserIdQuery(1L));

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void handleGetByUserId_ShouldReturnEmptyList_WhenUserHasNoSimulations() {
        // Arrange
        when(repository.findByUserIdOrderByCreatedAtDesc(2L)).thenReturn(List.of());

        // Act
        List<LoanSimulation> result = queryService.handle(new GetLoanSimulationsByUserIdQuery(2L));

        // Assert
        assertTrue(result.isEmpty());
    }
}
