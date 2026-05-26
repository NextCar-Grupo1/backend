package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.commands.SignInCommand;
import org.pe.nextcar.iam.interfaces.rest.resources.SignInResource;

/** SignInCommandFromResourceAssembler type. */
public class SignInCommandFromResourceAssembler {
  /** To command from resource. */
  public static SignInCommand toCommandFromResource(SignInResource resource) {
    return new SignInCommand(resource.email(), resource.password());
  }
}
