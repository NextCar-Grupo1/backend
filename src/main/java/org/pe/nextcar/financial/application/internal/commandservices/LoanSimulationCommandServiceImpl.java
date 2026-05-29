package org.pe.nextcar.financial.application.internal.commandservices;

import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.commands.CreateLoanSimulationCommand;
import org.pe.nextcar.financial.domain.services.LoanCalculationService;
import org.pe.nextcar.financial.domain.services.LoanCalculationService.CalculationInput;
import org.pe.nextcar.financial.domain.services.LoanSimulationCommandService;
import org.pe.nextcar.financial.infrastructure.persistence.jpa.repositories.LoanSimulationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LoanSimulationCommandServiceImpl implements LoanSimulationCommandService {

    private final LoanSimulationRepository repository;
    private final LoanCalculationService   calculationService;

    public LoanSimulationCommandServiceImpl(LoanSimulationRepository repository,
                                            LoanCalculationService calculationService) {
        this.repository         = repository;
        this.calculationService = calculationService;
    }

    @Override
    public Optional<LoanSimulation> handle(CreateLoanSimulationCommand cmd) {

        // Validate grace period against bank policy
        int maxGrace = cmd.financialEntity().getMaxGracePeriodMonths();
        if (cmd.gracePeriodMonths() > maxGrace) {
            throw new IllegalArgumentException(
                    "El periodo de gracia máximo para " + cmd.financialEntity().getDisplayName()
                            + " es de " + maxGrace + " meses.");
        }
        if (cmd.gracePeriodMonths() >= cmd.termMonths()) {
            throw new IllegalArgumentException(
                    "Los periodos de gracia no pueden ser iguales o mayores al plazo total.");
        }
        if (cmd.initialFeeRate() < 0.20 || cmd.initialFeeRate() > 0.80) {
            throw new IllegalArgumentException(
                    "La cuota inicial debe estar entre 20% y 80% del precio del vehículo.");
        }

        // Build aggregate
        var simulation = new LoanSimulation(
                cmd.userId(), cmd.currency(), cmd.vehiclePrice(), cmd.initialFeeRate(),
                cmd.termMonths(), cmd.startDate(), cmd.paymentMethod(), cmd.rateType(),
                cmd.rateValue(), cmd.capitalizationFrequency(), cmd.gracePeriodType(),
                cmd.gracePeriodMonths(), cmd.financialEntity(), cmd.desgravamenRate(),
                cmd.vehicleInsuranceMonthly(), cmd.portesMonthly()
        );

        // Run financial algorithm
        var input = new CalculationInput(
                cmd.vehiclePrice(), simulation.getPrincipal(), cmd.termMonths(),
                cmd.startDate(), cmd.paymentMethod(), cmd.rateType(), cmd.rateValue(),
                cmd.capitalizationFrequency(), cmd.gracePeriodType(), cmd.gracePeriodMonths(),
                cmd.financialEntity(), cmd.desgravamenRate(), cmd.vehicleInsuranceMonthly(),
                cmd.portesMonthly()
        );
        var result = calculationService.calculate(input);

        // Apply results and persist
        simulation.applyCalculationResult(
                result.monthlyEffectiveRate(), result.baseInstallment(),
                result.totalMonthlyInstallment(), result.npv(), result.monthlyIrr(),
                result.tcea(), result.totalInterestPaid(), result.totalInsurancePaid(),
                result.totalAmortization(), result.totalPaid(), result.schedule()
        );

        return Optional.of(repository.save(simulation));
    }
}
