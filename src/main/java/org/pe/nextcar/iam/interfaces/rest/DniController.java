package org.pe.nextcar.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.pe.nextcar.iam.application.internal.outboundservices.dni.DniVerificationService;
import org.pe.nextcar.iam.interfaces.rest.resources.DniDataResource;
import org.pe.nextcar.iam.interfaces.rest.transform.DniDataResourceFromEntityAssembler;

@RestController
@RequestMapping(value = "/api/v1/dni", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "DNI", description = "Peruvian DNI lookup and validation")
public class DniController {
    private final DniVerificationService dniVerificationService;

    public DniController(DniVerificationService dniVerificationService) {
        this.dniVerificationService = dniVerificationService;
    }

    @GetMapping("/{dni}")
    public ResponseEntity<DniDataResource> lookupDni(@PathVariable String dni) {
        var result = dniVerificationService.verifyDni(dni);
        if (!result.valid()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DniDataResourceFromEntityAssembler.toResourceFrom(result));
    }
}
