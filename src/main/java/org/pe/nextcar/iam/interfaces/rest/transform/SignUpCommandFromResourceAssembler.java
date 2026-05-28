package org.pe.nextcar.iam.interfaces.rest.transform;

import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

/** SignUpCommandFromResourceAssembler type. */
public class SignUpCommandFromResourceAssembler {
  public static SignUpCommand toCommandFromResource(SignUpResource resource) {
    return new SignUpCommand(
            resource.email(),
            resource.password(),
            resource.firstName(),
            resource.lastName(),
            resource.phone(),
            resource.documentNumber(),  // nuevo
            resource.captchaToken());
  }
}
