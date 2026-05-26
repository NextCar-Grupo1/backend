package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.interfaces.rest.resources.RoleResource;

/** RoleResourceFromEntityAssembler type. */
public class RoleResourceFromEntityAssembler {
  /** To resource from entity. */
  public static RoleResource toResourceFromEntity(Role entity) {
    return new RoleResource(entity.getId(), entity.getStringName());
  }
}
