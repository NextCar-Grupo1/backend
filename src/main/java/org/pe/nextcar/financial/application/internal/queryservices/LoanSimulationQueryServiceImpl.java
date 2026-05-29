package org.pe.nextcar.financial.application.internal.queryservices;

import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationByIdQuery;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationsByUserIdQuery;
import org.pe.nextcar.financial.domain.services.LoanSimulationQueryService;
import org.pe.nextcar.financial.infrastructure.persistence.jpa.repositories.LoanSimulationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LoanSimulationQueryServiceImpl implements LoanSimulationQueryService {

    private final LoanSimulationRepository repository;

    public LoanSimulationQueryServiceImpl(LoanSimulationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<LoanSimulation> handle(GetLoanSimulationByIdQuery query) {
        return repository.findById(query.simulationId());
    }

    @Override
    public List<LoanSimulation> handle(GetLoanSimulationsByUserIdQuery query) {
        return repository.findByUserIdOrderByCreatedAtDesc(query.userId());
    }
}