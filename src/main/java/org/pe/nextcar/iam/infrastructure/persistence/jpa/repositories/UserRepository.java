package org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.pe.nextcar.iam.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @EntityGraph(attributePaths = "roles")
  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findById(Long id);

  @EntityGraph(attributePaths = "roles")
  List<User> findAll();

  boolean existsByEmail(String email);
}
