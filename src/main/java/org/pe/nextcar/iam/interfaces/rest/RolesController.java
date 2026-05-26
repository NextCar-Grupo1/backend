package org.pe.nextcar.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import  org.pe.nextcar.iam.domain.model.queries.GetAllRolesQuery;
import  org.pe.nextcar.iam.domain.services.RoleQueryService;
import  org.pe.nextcar.iam.interfaces.rest.resources.RoleResource;
import  org.pe.nextcar.iam.interfaces.rest.transform.RoleResourceFromEntityAssembler;

import java.util.List;

/** RolesController type. */
@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles", description = "Role Management Endpoints")
public class RolesController {
  private final RoleQueryService roleQueryService;

  /** Constructs a new RolesController. */
  public RolesController(RoleQueryService roleQueryService) {
    this.roleQueryService = roleQueryService;
  }

  /** Get all roles. */
  @GetMapping
  public ResponseEntity<List<RoleResource>> getAllRoles() {
    var getAllRolesQuery = new GetAllRolesQuery();
    var roles = roleQueryService.handle(getAllRolesQuery);
    var roleResources =
        roles.stream().map(RoleResourceFromEntityAssembler::toResourceFromEntity).toList();
    return ResponseEntity.ok(roleResources);
  }
}
