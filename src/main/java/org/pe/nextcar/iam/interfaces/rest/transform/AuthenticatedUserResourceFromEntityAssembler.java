package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.interfaces.rest.resources.AuthenticatedUserResource;

import java.util.Set;
import java.util.stream.Collectors;

/** AuthenticatedUserResourceFromEntityAssembler type. */
public class AuthenticatedUserResourceFromEntityAssembler {
  /** To resource from entity. */
  public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
    Set<String> roleNames =
        user.getRoles().stream().map(Role::getStringName).collect(Collectors.toSet());
    return new AuthenticatedUserResource(user.getId(), user.getEmail(), token, roleNames);
  }
}
