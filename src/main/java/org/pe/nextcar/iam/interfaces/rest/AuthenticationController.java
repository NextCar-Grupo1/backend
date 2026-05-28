package org.pe.nextcar.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import  org.pe.nextcar.iam.domain.services.UserCommandService;
import  org.pe.nextcar.iam.interfaces.rest.resources.AuthenticatedUserResource;
import  org.pe.nextcar.iam.interfaces.rest.resources.SignInResource;
import  org.pe.nextcar.iam.interfaces.rest.resources.SignUpResource;
import  org.pe.nextcar.iam.interfaces.rest.resources.UserResource;
import  org.pe.nextcar.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import  org.pe.nextcar.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import  org.pe.nextcar.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import  org.pe.nextcar.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

/** AuthenticationController type. */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication Endpoints")
public class AuthenticationController {
  private final UserCommandService userCommandService;

  /** Constructs a new AuthenticationController. */
  public AuthenticationController(UserCommandService userCommandService) {
    this.userCommandService = userCommandService;
  }

  @PostMapping("/sign-in")
  public ResponseEntity<AuthenticatedUserResource> signIn(
      @Valid @RequestBody SignInResource signInResource) {
    var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
    var authenticatedUser = userCommandService.handle(signInCommand);
    if (authenticatedUser.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    var result = authenticatedUser.get();
    var authenticatedUserResource =
        AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
            result.user(), result.token());
    return ResponseEntity.ok(authenticatedUserResource);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpResource signUpResource) {
    var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
    var user = userCommandService.handle(signUpCommand);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
    return new ResponseEntity<>(userResource, HttpStatus.CREATED);
  }
}
