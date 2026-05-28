package org.pe.nextcar.iam.infrastructure.dni.apiperu;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiPeruDniData(
        @JsonProperty("numero") String dni,
        String nombres,
        @JsonProperty("apellido_paterno") String apellidoPaterno,
        @JsonProperty("apellido_materno") String apellidoMaterno
) {}
