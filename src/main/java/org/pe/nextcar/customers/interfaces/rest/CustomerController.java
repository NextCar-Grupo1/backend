package org.pe.nextcar.customers.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pe.nextcar.customers.domain.model.queries.*;
import org.pe.nextcar.customers.domain.services.*;
import org.pe.nextcar.customers.interfaces.rest.resources.*;
import org.pe.nextcar.customers.interfaces.rest.transform.CustomerAssembler;
import org.pe.nextcar.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.pe.nextcar.iam.interfaces.acl.IamContextFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Customers", description = "Perfil financiero del cliente")
public class CustomerController {

    private final CustomerCommandService commandService;
    private final CustomerQueryService   queryService;
    private final IamContextFacade       iamFacade;

    public CustomerController(CustomerCommandService commandService,
                              CustomerQueryService queryService,
                              IamContextFacade iamFacade) {
        this.commandService = commandService;
        this.queryService   = queryService;
        this.iamFacade      = iamFacade;
    }

    /** GET /api/v1/customers/me — perfil del usuario autenticado */
    @Operation(summary = "Obtener mi perfil de cliente")
    @GetMapping("/me")
    public ResponseEntity<CustomerResource> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = iamFacade.fetchUserIdByEmail(currentUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return queryService.handle(new GetCustomerByUserIdQuery(userId))
                .map(c -> ResponseEntity.ok(CustomerAssembler.toResource(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/v1/customers — crear perfil */
    @Operation(summary = "Crear perfil de cliente",
            description = "Se ejecuta una sola vez después del registro. Completa los datos financieros.")
    @PostMapping
    public ResponseEntity<CustomerResource> createProfile(
            @Valid @RequestBody CreateCustomerProfileResource resource,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Long userId = iamFacade.fetchUserIdByEmail(currentUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        var command  = CustomerAssembler.toCommand(userId, resource);
        var customer = commandService.handle(command);
        return customer
                .map(c -> new ResponseEntity<>(CustomerAssembler.toResource(c), HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }

    /** PUT /api/v1/customers/{id} — actualizar perfil */
    @Operation(summary = "Actualizar perfil de cliente")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResource> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerProfileResource resource) {
        var command  = CustomerAssembler.toCommand(id, resource);
        var customer = commandService.handle(command);
        return customer
                .map(c -> ResponseEntity.ok(CustomerAssembler.toResource(c)))
                .orElse(ResponseEntity.notFound().build());
    }
}