package org.pe.nextcar.financial.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationByIdQuery;
import org.pe.nextcar.financial.domain.model.queries.GetLoanSimulationsByUserIdQuery;
import org.pe.nextcar.financial.domain.model.valueobjects.FinancialEntity;
import org.pe.nextcar.financial.domain.services.LoanSimulationCommandService;
import org.pe.nextcar.financial.domain.services.LoanSimulationQueryService;
import org.pe.nextcar.financial.interfaces.rest.resources.*;
import org.pe.nextcar.financial.interfaces.rest.transform.LoanSimulationAssembler;
import org.pe.nextcar.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.pe.nextcar.iam.interfaces.acl.IamContextFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/loan-simulations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Loan Simulations", description = "Vehicle loan simulation — French method & Smart Purchase")
public class LoanSimulationController {

    private final LoanSimulationCommandService commandService;
    private final LoanSimulationQueryService   queryService;
    private final IamContextFacade             iamFacade;

    public LoanSimulationController(LoanSimulationCommandService commandService,
                                    LoanSimulationQueryService queryService,
                                    IamContextFacade iamFacade) {
        this.commandService = commandService;
        this.queryService   = queryService;
        this.iamFacade      = iamFacade;
    }

    // ── POST /api/v1/loan-simulations ──────────────────────────────────────────

    @Operation(
            summary = "Crear simulación de crédito vehicular",
            description = "Calcula el cronograma completo (método Francés o Compra Inteligente). " +
                    "Incluye TEM, cuota mensual, VAN, TIR y TCEA. Requiere JWT."
    )
    @ApiResponse(responseCode = "201", description = "Simulación creada con cronograma completo")
    @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido")
    @PostMapping
    public ResponseEntity<LoanSimulationResource> createSimulation(
            @Valid @RequestBody CreateLoanSimulationResource resource,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        // Resolve userId via IAM facade using the authenticated user's email
        Long userId = iamFacade.fetchUserIdByEmail(currentUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var command    = LoanSimulationAssembler.toCommand(userId, resource);
        var simulation = commandService.handle(command);

        return simulation
                .map(s -> new ResponseEntity<>(LoanSimulationAssembler.toResource(s), HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }

    // ── GET /api/v1/loan-simulations/{id} ─────────────────────────────────────

    @Operation(summary = "Obtener simulación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<LoanSimulationResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetLoanSimulationByIdQuery(id))
                .map(s -> ResponseEntity.ok(LoanSimulationAssembler.toResource(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ── GET /api/v1/loan-simulations/my ───────────────────────────────────────

    @Operation(summary = "Mis simulaciones (usuario autenticado)")
    @GetMapping("/my")
    public ResponseEntity<List<LoanSimulationResource>> getMySimulations(
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        Long userId = iamFacade.fetchUserIdByEmail(currentUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var results = queryService.handle(new GetLoanSimulationsByUserIdQuery(userId))
                .stream().map(LoanSimulationAssembler::toResource).toList();
        return ResponseEntity.ok(results);
    }

    // ── GET /api/v1/loan-simulations/financial-entities ───────────────────────

    @Operation(summary = "Lista de entidades financieras con sus parámetros por defecto")
    @GetMapping("/financial-entities")
    public ResponseEntity<List<FinancialEntityResource>> getFinancialEntities() {
        var entities = Arrays.stream(FinancialEntity.values())
                .map(e -> new FinancialEntityResource(
                        e.name(), e.getDisplayName(),
                        e.getSmartPurchaseResidualRate(), e.getDefaultDesgravamenRate(),
                        e.getDefaultVehicleInsurance(), e.getDefaultPortes(),
                        e.getMaxGracePeriodMonths()
                )).toList();
        return ResponseEntity.ok(entities);
    }
}
