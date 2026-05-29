package org.pe.nextcar.financial.infrastructure.persistence.jpa.repositories;
import org.pe.nextcar.financial.domain.model.aggregates.LoanSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanSimulationRepository extends JpaRepository<LoanSimulation, Long> {
    List<LoanSimulation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
