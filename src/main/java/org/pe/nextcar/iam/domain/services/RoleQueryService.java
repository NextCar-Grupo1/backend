package org.pe.nextcar.iam.domain.services;

import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.queries.GetAllRolesQuery;
import org.pe.nextcar.iam.domain.model.queries.GetRoleByIdQuery;

import java.util.List;
import java.util.Optional;

/** RoleQueryService contract. */
public interface RoleQueryService {
  /** Handle. */
  List<Role> handle(GetAllRolesQuery query);

  /** Handle. */
  Optional<Role> handle(GetRoleByIdQuery query);
}
