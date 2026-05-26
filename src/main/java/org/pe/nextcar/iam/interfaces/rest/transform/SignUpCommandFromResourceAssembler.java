package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

/** SignUpCommandFromResourceAssembler type. */
public class SignUpCommandFromResourceAssembler {
  /** To command from resource. */
  public static SignUpCommand toCommandFromResource(SignUpResource resource) {
    var roles =
        resource.roles() != null
            ? resource.roles().stream().map(Role::toRoleFromName).toList()
            : new ArrayList<Role>();
    return new SignUpCommand(
        resource.email(),
        resource.password(),
        resource.firstName(),
        resource.lastName(),
        resource.phone(),
        roles);
  }
}
