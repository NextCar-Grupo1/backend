package org.pe.nextcar.iam.application.internal.queryservices;

import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.queries.GetAllRolesQuery;
import org.pe.nextcar.iam.domain.model.queries.GetRoleByIdQuery;
import org.pe.nextcar.iam.domain.services.RoleQueryService;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

/** RoleQueryServiceImpl type. */
@Service
public class RoleQueryServiceImpl implements RoleQueryService {
  private final RoleRepository roleRepository;

  /** Constructs a new RoleQueryServiceImpl. */
  public RoleQueryServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public List<Role> handle(GetAllRolesQuery query) {
    return roleRepository.findAll();
  }

  @Override
  public Optional<Role> handle(GetRoleByIdQuery query) {
    return roleRepository.findById(query.roleId());
  }
}
