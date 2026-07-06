package org.pe.nextcar.financial.application.internal.commandservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.commands.CreateLoanSimulationCommand;
import org.pe.nextcar.financial.domain.model.valueobjects.*;
import org.pe.nextcar.financial.domain.services.LoanCalculationService;
import org.pe.nextcar.financial.infrastructure.persistence.jpa.repositories.LoanSimulationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanSimulationCommandServiceImplTest {

    @Mock
    private LoanSimulationRepository repository;

    @Mock
    private LoanCalculationService calculationService;

    @InjectMocks
    private LoanSimulationCommandServiceImpl commandService;

    private CreateLoanSimulationCommand baseCommand(int gracePeriodMonths, int termMonths, double initialFeeRate) {
        return new CreateLoanSimulationCommand(
                1L, LoanCurrency.SOLES, 89900.0, initialFeeRate, termMonths,
                LocalDate.of(2026, 1, 15), PaymentMethod.FRENCH, RateType.TEA, 0.125,
                CapitalizationFrequency.MONTHLY, GracePeriodType.NONE, gracePeriodMonths,
                FinancialEntity.BCP, 0.0005, 150.0, 10.0);
    }

    private LoanCalculationService.CalculationResult fakeResult() {
        return new LoanCalculationService.CalculationResult(
                0.0098, 2380.0, 2580.0, 0.0, 0.0104, 0.13,
                13000.0, 7000.0, 71920.0, 93000.0, List.of());
    }

    @Test
    void handle_ShouldSaveSimulation_WhenParametersAreValid() {
        // Arrange
        CreateLoanSimulationCommand command = baseCommand(0, 36, 0.20);
        when(calculationService.calculate(any())).thenReturn(fakeResult());
        when(repository.save(any(LoanSimulation.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Optional<LoanSimulation> result = commandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(71920.0, result.get().getPrincipal(), 0.01);
        ArgumentCaptor<LoanSimulation> captor = ArgumentCaptor.forClass(LoanSimulation.class);
        verify(repository).save(captor.capture());
        assertEquals(0.13, captor.getValue().getTcea());
    }

    @Test
    void handle_ShouldThrow_WhenGracePeriodExceedsBankMaximum() {
        // Arrange: BCP's max grace period is 6 months
        CreateLoanSimulationCommand command = baseCommand(7, 36, 0.20);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commandService.handle(command));
        assertTrue(ex.getMessage().contains("periodo de gracia"));
        verify(repository, never()).save(any());
        verifyNoInteractions(calculationService);
    }

    @Test
    void handle_ShouldThrow_WhenGracePeriodIsGreaterOrEqualToTerm() {
        // Arrange
        CreateLoanSimulationCommand command = baseCommand(6, 6, 0.20);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(repository, never()).save(any());
    }

    @Test
    void handle_ShouldThrow_WhenInitialFeeRateBelowMinimum() {
        // Arrange
        CreateLoanSimulationCommand command = baseCommand(0, 36, 0.10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(repository, never()).save(any());
    }

    @Test
    void handle_ShouldThrow_WhenInitialFeeRateAboveMaximum() {
        // Arrange
        CreateLoanSimulationCommand command = baseCommand(0, 36, 0.90);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(repository, never()).save(any());
    }
}
