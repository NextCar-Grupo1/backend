package org.pe.nextcar.iam.application.acl;

import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.queries.GetUserByEmailQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByIdQuery;
import org.pe.nextcar.iam.domain.services.UserCommandService;
import org.pe.nextcar.iam.domain.services.UserQueryService;
import org.pe.nextcar.iam.interfaces.acl.IamContextFacade;
import org.pe.nextcar.iam.interfaces.rest.resources.UserResource;
import org.pe.nextcar.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class IamContextFacadeImpl implements IamContextFacade {
  private final UserCommandService userCommandService;
  private final UserQueryService userQueryService;

  public IamContextFacadeImpl(
      UserCommandService userCommandService, UserQueryService userQueryService) {
    this.userCommandService = userCommandService;
    this.userQueryService = userQueryService;
  }

  @Override
  public Optional<Long> createUser(
      String email, String password, String firstName, String lastName, String phone) {
    var signUpCommand = new SignUpCommand(
        email, password, firstName, lastName, phone, null, null);
    return userCommandService.handle(signUpCommand).map(User::getId);
  }

  @Override
  public Optional<Long> createUser(
      String email, String password, String firstName, String lastName,
      String phone, String documentNumber, String captchaToken, List<String> roleNames) {
    if (roleNames == null) roleNames = new ArrayList<>();
    var roles = roleNames.stream().map(Role::toRoleFromName).toList();
    var signUpCommand = new SignUpCommand(
        email, password, firstName, lastName, phone, documentNumber, captchaToken);
    return userCommandService.handle(signUpCommand).map(User::getId);
  }

  @Override
  public Optional<UserResource> fetchUserById(Long userId) {
    var query = new GetUserByIdQuery(userId);
    return userQueryService.handle(query)
        .map(UserResourceFromEntityAssembler::toResourceFromEntity);
  }

  @Override
  public Optional<Long> fetchUserIdByEmail(String email) {
    var query = new GetUserByEmailQuery(email);
    return userQueryService.handle(query).map(User::getId);
  }

  @Override
  public boolean existsUserByEmailAndIdIsNot(String email, Long id) {
    var query = new GetUserByEmailQuery(email);
    return userQueryService.handle(query)
        .map(user -> !Objects.equals(user.getId(), id))
        .orElse(false);
  }

  @Override
  public boolean existsUserById(Long id) {
    var query = new GetUserByIdQuery(id);
    return userQueryService.handle(query).isPresent();
  }

  @Override
  public Optional<String> fetchEmailByUserId(Long userId) {
    var query = new GetUserByIdQuery(userId);
    return userQueryService.handle(query).map(User::getEmail);
  }

  @Override
  public boolean existsUserByRole(Long userId, String roleName) {
    var query = new GetUserByIdQuery(userId);
    return userQueryService.handle(query)
        .map(user -> user.getRoles().stream().anyMatch(role -> role.getStringName().equals(roleName)))
        .orElse(false);
  }
}
