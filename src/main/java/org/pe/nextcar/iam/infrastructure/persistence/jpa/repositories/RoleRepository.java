package org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;

import java.util.Optional;

/** RoleRepository contract. */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  /** Find by name. */
  Optional<Role> findByName(Roles name);

  /** Exists by name. */
  boolean existsByName(Roles name);
}
