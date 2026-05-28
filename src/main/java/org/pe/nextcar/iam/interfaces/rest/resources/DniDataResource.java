package org.pe.nextcar.iam.interfaces.rest.resources;

public record DniDataResource(
        String dni,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno
) {}
