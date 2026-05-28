package org.pe.nextcar.iam.infrastructure.dni.apiperu;

public record ApiPeruResponse<T>(boolean success, T data) {}
