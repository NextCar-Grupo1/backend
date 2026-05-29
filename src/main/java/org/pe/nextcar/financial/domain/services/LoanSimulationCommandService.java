package org.pe.nextcar.financial.domain.services;

import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.commands.CreateLoanSimulationCommand;
import java.util.Optional;

public interface LoanSimulationCommandService {
    Optional<LoanSimulation> handle(CreateLoanSimulationCommand command);
}
