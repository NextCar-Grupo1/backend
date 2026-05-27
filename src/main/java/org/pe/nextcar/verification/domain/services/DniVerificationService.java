package org.pe.nextcar.verification.domain.services;

import org.pe.nextcar.verification.domain.model.valueobjects.DniResult;

public interface DniVerificationService {
    DniResult verifyDni(String dni);
    boolean isAdult(DniResult result); // >= 18 años
}