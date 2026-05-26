package org.pe.nextcar.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import  org.pe.nextcar.iam.domain.model.queries.GetAllUsersQuery;
import  org.pe.nextcar.iam.domain.model.queries.GetUserByIdQuery;
import  org.pe.nextcar.iam.domain.services.UserQueryService;
import  org.pe.nextcar.iam.interfaces.rest.resources.UserResource;
import  org.pe.nextcar.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;

import java.util.List;

/** UsersController type. */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User Management Endpoints")
public class UsersController {
  private final UserQueryService userQueryService;

  /** Constructs a new UsersController. */
  public UsersController(UserQueryService userQueryService) {
    this.userQueryService = userQueryService;
  }

  /** Get all users. */
  @GetMapping
  public ResponseEntity<List<UserResource>> getAllUsers() {
    var getAllUsersQuery = new GetAllUsersQuery();
    var users = userQueryService.handle(getAllUsersQuery);
    var userResources =
        users.stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
    return ResponseEntity.ok(userResources);
  }

  /** Get user by id. */
  @GetMapping(value = "/{userId}")
  public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
    var getUserByIdQuery = new GetUserByIdQuery(userId);
    var user = userQueryService.handle(getUserByIdQuery);
    if (user.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
    return ResponseEntity.ok(userResource);
  }
}
