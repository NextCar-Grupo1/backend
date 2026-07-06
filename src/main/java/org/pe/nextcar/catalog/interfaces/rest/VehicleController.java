package org.pe.nextcar.catalog.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pe.nextcar.catalog.domain.model.commands.DeleteVehicleCommand;
import org.pe.nextcar.catalog.domain.model.queries.GetAllVehiclesQuery;
import org.pe.nextcar.catalog.domain.model.queries.GetVehicleByIdQuery;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.pe.nextcar.catalog.domain.services.VehicleCommandService;
import org.pe.nextcar.catalog.domain.services.VehicleQueryService;
import org.pe.nextcar.catalog.interfaces.rest.resources.CreateVehicleResource;
import org.pe.nextcar.catalog.interfaces.rest.resources.UpdateVehicleResource;
import org.pe.nextcar.catalog.interfaces.rest.resources.VehicleResource;
import org.pe.nextcar.catalog.interfaces.rest.transform.VehicleAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicles", description = "Catálogo de vehículos disponibles para financiamiento")
public class VehicleController {

    private final VehicleQueryService   queryService;
    private final VehicleCommandService commandService;

    public VehicleController(VehicleQueryService queryService,
                             VehicleCommandService commandService) {
        this.queryService   = queryService;
        this.commandService = commandService;
    }

    /**
     * GET /api/v1/vehicles
     * GET /api/v1/vehicles?category=SUV
     * Devuelve todos los vehículos disponibles, opcionalmente filtrados por categoría.
     * Cada vehículo incluye imageUrl y estimatedMonthlyPayment para mostrar en la card.
     */
    @Operation(
        summary = "Catálogo de vehículos",
        description = "Lista todos los vehículos disponibles con imagen y cuota estimada. " +
            "Filtro opcional por categoría: SEDAN, SUV, HATCHBACK, PICKUP, VAN, LUXURY."
    )
    @GetMapping
    public ResponseEntity<List<VehicleResource>> getAll(
        @RequestParam(required = false) VehicleCategory category
    ) {
        var query   = new GetAllVehiclesQuery(category);
        var results = queryService.handle(query)
            .stream().map(VehicleAssembler::toResource).toList();
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/v1/vehicles/{id}
     * Devuelve un vehículo específico con todos sus detalles.
     * El frontend usa `price` y `currency` para pre-llenar el formulario de simulación.
     */
    @Operation(
        summary = "Detalle de vehículo",
        description = "Obtiene un vehículo por ID. Usa el campo `price` para pre-llenar el simulador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetVehicleByIdQuery(id))
            .map(v -> ResponseEntity.ok(VehicleAssembler.toResource(v)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ── Administración del catálogo (ROLE_ADMIN) ──────────────────────────────

    /**
     * POST /api/v1/vehicles
     * Crea un nuevo vehículo en el catálogo. Requiere rol ADMIN.
     */
    @Operation(
        summary = "Crear vehículo (admin)",
        description = "Agrega un nuevo vehículo al catálogo. Recalcula automáticamente " +
            "la cuota estimada (estimatedMonthlyPayment). Requiere rol ADMIN."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VehicleResource> create(@Valid @RequestBody CreateVehicleResource resource) {
        var command = VehicleAssembler.toCommand(resource);
        var vehicle = commandService.handle(command);
        return vehicle
            .map(v -> new ResponseEntity<>(VehicleAssembler.toResource(v), HttpStatus.CREATED))
            .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * PUT /api/v1/vehicles/{id}
     * Actualiza un vehículo existente. Requiere rol ADMIN.
     */
    @Operation(
        summary = "Actualizar vehículo (admin)",
        description = "Edita los datos de un vehículo y recalcula su cuota estimada. Requiere rol ADMIN."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResource> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateVehicleResource resource) {
        var command = VehicleAssembler.toCommand(id, resource);
        var vehicle = commandService.handle(command);
        return vehicle
            .map(v -> ResponseEntity.ok(VehicleAssembler.toResource(v)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/vehicles/{id}
     * Da de baja (soft-delete) un vehículo: deja de listarse en el catálogo pero
     * se conserva en BD para no romper la trazabilidad de simulaciones existentes.
     * Requiere rol ADMIN.
     */
    @Operation(
        summary = "Eliminar vehículo (admin)",
        description = "Marca el vehículo como no disponible (soft-delete). Requiere rol ADMIN."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandService.handle(new DeleteVehicleCommand(id));
        return ResponseEntity.noContent().build();
    }
}