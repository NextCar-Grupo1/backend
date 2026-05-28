package org.pe.nextcar.iam.application.internal.outboundservices.dni;

import org.pe.nextcar.iam.domain.model.valueobjects.DniResult;

public interface DniVerificationService {
    DniResult verifyDni(String dni);
    boolean isAdult(DniResult result);
}
