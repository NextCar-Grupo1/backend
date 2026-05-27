package org.pe.nextcar.verification.domain.model.valueobjects;

import java.time.LocalDate;
import org.pe.nextcar.verification.domain.model.valueobjects.DniVerificationStatus;
public record DniResult(
        String dni,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        LocalDate fechaNacimiento,
        boolean valid,
        DniVerificationStatus status  // ← nuevo campo
) {}

