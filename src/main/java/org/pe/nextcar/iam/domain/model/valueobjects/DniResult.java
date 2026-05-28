package org.pe.nextcar.iam.domain.model.valueobjects;

import java.time.LocalDate;

public record DniResult(
        String dni,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        LocalDate fechaNacimiento,
        boolean valid,
        DniVerificationStatus status
) {}
