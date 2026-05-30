package org.pe.nextcar.catalog.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pe.nextcar.catalog.domain.model.queries.GetAllVehiclesQuery;
import org.pe.nextcar.catalog.domain.model.queries.GetVehicleByIdQuery;
import org.pe.nextcar.catalog.domain.model.valueobjects.VehicleCategory;
import org.pe.nextcar.catalog.domain.services.VehicleQueryService;
import org.pe.nextcar.catalog.interfaces.rest.resources.VehicleResource;
import org.pe.nextcar.catalog.interfaces.rest.transform.VehicleAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicles", description = "Catálogo de vehículos disponibles para financiamiento")
public class VehicleController {

    private final VehicleQueryService queryService;

    public VehicleController(VehicleQueryService queryService) {
        this.queryService = queryService;
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
}