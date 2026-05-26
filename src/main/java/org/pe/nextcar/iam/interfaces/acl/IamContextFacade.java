package org.pe.nextcar.iam.interfaces.acl;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import  org.pe.nextcar.iam.domain.model.queries.GetUserByEmailQuery;
import  org.pe.nextcar.iam.domain.model.queries.GetUserByIdQuery;
import  org.pe.nextcar.iam.domain.services.UserCommandService;
import  org.pe.nextcar.iam.domain.services.UserQueryService;
import  org.pe.nextcar.iam.interfaces.rest.resources.UserResource;
import  org.pe.nextcar.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** IamContextFacade type. */
@Service
public class IamContextFacade {
  private final UserCommandService userCommandService;
  private final UserQueryService userQueryService;

  /** Constructs a new IamContextFacade. */
  public IamContextFacade(
      UserCommandService userCommandService, UserQueryService userQueryService) {
    this.userCommandService = userCommandService;
    this.userQueryService = userQueryService;
  }

  /** Create user. */
  public Long createUser(
      String email, String password, String firstName, String lastName, String phone) {
    var defaultRole = Role.toRoleFromName("ROLE_TOURIST");
    var signUpCommand =
        new SignUpCommand(email, password, firstName, lastName, phone, List.of(defaultRole));
    var result = userCommandService.handle(signUpCommand);
    if (result.isEmpty()) {
      return 0L;
    }
    return result.get().getId();
  }

  /** Create user. */
  public Long createUser(
      String email,
      String password,
      String firstName,
      String lastName,
      String phone,
      List<String> roleNames) {
    if (roleNames == null) {
      roleNames = new ArrayList<>();
    }
    var roles = roleNames.stream().map(Role::toRoleFromName).toList();

    var signUpCommand = new SignUpCommand(email, password, firstName, lastName, phone, roles);
    var result = userCommandService.handle(signUpCommand);
    if (result.isEmpty()) {
      return 0L;
    }
    return result.get().getId();
  }

  /** Fetch user by id. */
  public Optional<UserResource> fetchUserById(Long userId) {
    var getUserByIdQuery = new GetUserByIdQuery(userId);
    var result = userQueryService.handle(getUserByIdQuery);
    if (result.isEmpty()) {
      return Optional.empty();
    }
    var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(result.get());
    return Optional.of(userResource);
  }

  /** Fetch user id by email. */
  public Long fetchUserIdByEmail(String email) {
    var getUserByEmailQuery = new GetUserByEmailQuery(email);
    var result = userQueryService.handle(getUserByEmailQuery);
    if (result.isEmpty()) {
      return 0L;
    }
    return result.get().getId();
  }

  /** Exists user by email and id is not. */
  public boolean existsUserByEmailAndIdIsNot(String email, Long id) {
    var getUserByEmailQuery = new GetUserByEmailQuery(email);
    var result = userQueryService.handle(getUserByEmailQuery);
    if (result.isEmpty()) {
      return false;
    }
    return !Objects.equals(result.get().getId(), id);
  }

  /** Exists user by id. */
  public boolean existsUserById(Long id) {
    var getUserByIdQuery = new GetUserByIdQuery(id);
    var result = userQueryService.handle(getUserByIdQuery);
    return result.isPresent();
  }

  /** Fetch email by user id. */
  public String fetchEmailByUserId(Long userId) {
    var getUserByIdQuery = new GetUserByIdQuery(userId);
    var result = userQueryService.handle(getUserByIdQuery);
    if (result.isEmpty()) {
      return Strings.EMPTY;
    }
    return result.get().getEmail();
  }

  /** Exists user by role. */
  public boolean existsUserByRole(Long userId, String roleName) {
    var query = new GetUserByIdQuery(userId);
    var result = userQueryService.handle(query);
    if (result.isEmpty()) {
      return false;
    }

    return result.get().getRoles().stream().anyMatch(role -> role.getStringName().equals(roleName));
  }
}
