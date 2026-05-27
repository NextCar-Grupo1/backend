package org.pe.nextcar.verification.infrastructure.external.reniec.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReniecApiResponse(
        String dni,
        String nombres,
        @JsonProperty("apellido_paterno") String apellidoPaterno,
        @JsonProperty("apellido_materno") String apellidoMaterno,
        @JsonProperty("fecha_nacimiento") String fechaNacimiento // "DD/MM/YYYY"
) {}