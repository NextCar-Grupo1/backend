package org.pe.nextcar.iam.domain.services;

import org.pe.nextcar.iam.domain.model.commands.SeedRolesCommand;

/** RoleCommandService contract. */
public interface RoleCommandService {
  /** Handle. */
  void handle(SeedRolesCommand command);
}
