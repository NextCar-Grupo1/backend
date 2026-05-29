package org.pe.nextcar.financial.domain.services;

import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationByIdQuery;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface LoanSimulationQueryService {
    Optional<LoanSimulation> handle(GetLoanSimulationByIdQuery query);
    List<LoanSimulation>     handle(GetLoanSimulationsByUserIdQuery query);
}
 