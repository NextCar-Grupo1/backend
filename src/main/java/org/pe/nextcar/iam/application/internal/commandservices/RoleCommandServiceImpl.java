package org.pe.nextcar.iam.application.internal.commandservices;

import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.domain.model.commands.SeedRolesCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.iam.domain.services.RoleCommandService;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;

import java.util.Arrays;

/** RoleCommandServiceImpl type. */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {
  private final RoleRepository roleRepository;

  /** Constructs a new RoleCommandServiceImpl. */
  public RoleCommandServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public void handle(SeedRolesCommand command) {
    Arrays.stream(Roles.values())
        .forEach(
            role -> {
              if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(Roles.valueOf(role.name())));
              }
            });
  }
}
