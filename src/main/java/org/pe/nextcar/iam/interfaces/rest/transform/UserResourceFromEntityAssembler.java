package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.interfaces.rest.resources.UserResource;

/** UserResourceFromEntityAssembler type. */
public class UserResourceFromEntityAssembler {
  /** To resource from entity. */
  public static UserResource toResourceFromEntity(User entity) {
    var roles = entity.getRoles().stream().map(Role::getStringName).toList();
    return new UserResource(
        entity.getId(),
        entity.getEmail(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getPhone(),
        roles);
  }
}
