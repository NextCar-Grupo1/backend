package org.pe.nextcar.verification.domain.model.valueobjects;

public enum DniVerificationStatus {
    VALID,
    INVALID_DNI,
    UNDERAGE,
    API_UNAVAILABLE   // ← nuevo: permite distinguir el origen del fallo
}