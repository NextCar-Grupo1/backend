package org.pe.nextcar.financial.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pe.nextcar.financial.domain.model.entities.PaymentScheduleEntry;
import org.pe.nextcar.financial.domain.model.valueobjects.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure algorithm class with no external dependencies, so tests exercise the real
 * object directly (no Mockito collaborators are needed here) while still following
 * the Arrange-Act-Assert structure.
 */
class LoanCalculationServiceTest {

    private LoanCalculationService service;

    @BeforeEach
    void setUp() {
        // Arrange (shared)
        service = new LoanCalculationService();
    }

    @Test
    void toMonthlyEffectiveRate_ShouldConvertTeaCorrectly() {
        // Arrange
        double tea = 0.125;

        // Act
        double tem = service.toMonthlyEffectiveRate(tea, RateType.TEA, CapitalizationFrequency.MONTHLY);

        // Assert
        double expected = Math.pow(1.125, 1.0 / 12.0) - 1.0;
        assertEquals(expected, tem, 1e-9);
    }

    @Test
    void toMonthlyEffectiveRate_ShouldConvertTnaWithDailyCapitalization() {
        // Arrange
        double tna = 0.12;

        // Act
        double tem = service.toMonthlyEffectiveRate(tna, RateType.TNA, CapitalizationFrequency.DAILY);

        // Assert
        assertTrue(tem > 0);
        assertTrue(tem < tna); // monthly rate should be far smaller than the annual nominal rate
    }

    @Test
    void calculateFrenchInstallment_ShouldReturnEqualSplit_WhenRateIsZero() {
        // Arrange
        double principal = 12000.0;
        int months = 12;

        // Act
        double installment = service.calculateFrenchInstallment(principal, 0.0, months);

        // Assert
        assertEquals(1000.0, installment, 1e-9);
    }

    @Test
    void calculateFrenchInstallment_ShouldReturnPositiveInstallment_WhenRateIsPositive() {
        // Arrange
        double principal = 71920.0;
        double monthlyRate = 0.0098;
        int months = 36;

        // Act
        double installment = service.calculateFrenchInstallment(principal, monthlyRate, months);

        // Assert
        assertTrue(installment > 0);
        // Total paid must exceed principal because interest is charged
        assertTrue(installment * months > principal);
    }

    @Test
    void calculateSmartPurchaseInstallment_ShouldBeLowerThanFrenchInstallment_ForSamePrincipal() {
        // Arrange
        double principal = 71920.0;
        double residualValue = 35960.0;
        double monthlyRate = 0.0098;
        int months = 36;

        // Act
        double smartInstallment = service.calculateSmartPurchaseInstallment(principal, residualValue, monthlyRate, months);
        double frenchInstallment = service.calculateFrenchInstallment(principal, monthlyRate, months);

        // Assert
        assertTrue(smartInstallment < frenchInstallment,
                "Smart Purchase installment should be lower because part of the debt is deferred as a balloon payment");
    }

    @Test
    void calculate_ShouldGenerateScheduleWithExactlyTermMonthsEntries_ForFrenchNoGrace() {
        // Arrange
        var input = frenchInput(GracePeriodType.NONE, 0);

        // Act
        LoanCalculationService.CalculationResult result = service.calculate(input);

        // Assert
        assertEquals(36, result.schedule().size());
    }

    @Test
    void calculate_ShouldFullyAmortizeBalance_ByLastInstallment_ForFrenchNoGrace() {
        // Arrange
        var input = frenchInput(GracePeriodType.NONE, 0);

        // Act
        LoanCalculationService.CalculationResult result = service.calculate(input);

        // Assert
        List<PaymentScheduleEntry> schedule = result.schedule();
        double lastBalance = schedule.get(schedule.size() - 1).getFinalBalance();
        assertEquals(0.0, lastBalance, 0.5, "Final balance should be ~0 after rounding");
    }

    @Test
    void calculate_ShouldDecreaseOutstandingBalance_MonthOverMonth_WhenNoGracePeriod() {
        // Arrange
        var input = frenchInput(GracePeriodType.NONE, 0);

        // Act
        List<PaymentScheduleEntry> schedule = service.calculate(input).schedule();

        // Assert
        for (int i = 1; i < schedule.size(); i++) {
            assertTrue(schedule.get(i).getInitialBalance() <= schedule.get(i - 1).getInitialBalance(),
                    "Balance should not increase without a grace period");
        }
    }

    @Test
    void calculate_ShouldNotAmortizeCapital_DuringPartialGracePeriod() {
        // Arrange
        var input = frenchInput(GracePeriodType.PARTIAL, 3);

        // Act
        List<PaymentScheduleEntry> schedule = service.calculate(input).schedule();

        // Assert
        for (int i = 0; i < 3; i++) {
            assertEquals(0.0, schedule.get(i).getAmortization(), 1e-9);
            assertEquals(GracePeriodType.PARTIAL, schedule.get(i).getGracePeriodType());
        }
        assertTrue(schedule.get(3).getAmortization() > 0);
    }

    @Test
    void calculate_ShouldCapitalizeInterestAndChargeNothing_DuringTotalGracePeriod() {
        // Arrange
        var input = frenchInput(GracePeriodType.TOTAL, 2);

        // Act
        List<PaymentScheduleEntry> schedule = service.calculate(input).schedule();

        // Assert
        for (int i = 0; i < 2; i++) {
            assertEquals(0.0, schedule.get(i).getTotalInstallment(), 1e-9);
        }
        // Balance after the grace period should have grown compared to the original principal
        assertTrue(schedule.get(1).getFinalBalance() > input.principal());
    }



    @Test
    void calculateIRR_ShouldReturnPositiveRate_ForValidCashFlows() {
        // Arrange
        var input = frenchInput(GracePeriodType.NONE, 0);
        LoanCalculationService.CalculationResult result = service.calculate(input);

        // Act
        double irr = service.calculateIRR(input.principal(), result.schedule());

        // Assert
        assertTrue(irr > 0, "IRR should be positive since the client pays more than the principal received");
        assertTrue(irr < 1, "IRR should not be an absurdly high monthly rate for a normal simulation");
    }

    private LoanCalculationService.CalculationInput frenchInput(GracePeriodType graceType, int graceMonths) {
        double vehiclePrice = 89900.0;
        double principal = vehiclePrice * 0.80;
        return new LoanCalculationService.CalculationInput(
                vehiclePrice, principal, 36, LocalDate.of(2026, 1, 15),
                PaymentMethod.FRENCH, RateType.TEA, 0.125, CapitalizationFrequency.MONTHLY,
                graceType, graceMonths, FinancialEntity.BCP, 0.0005, 150.0, 10.0);
    }
}
