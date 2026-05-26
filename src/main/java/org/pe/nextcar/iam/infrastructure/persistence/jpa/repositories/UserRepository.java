package org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.pe.nextcar.iam.domain.model.aggregates.User;

import java.util.Optional;

/** UserRepository contract. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /** Find by email. */
  Optional<User> findByEmail(String email);

  /** Exists by email. */
  boolean existsByEmail(String email);
}
