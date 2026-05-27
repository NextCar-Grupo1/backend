package org.pe.nextcar.verification.interfaces.acl;

import org.pe.nextcar.verification.domain.model.valueobjects.DniResult;
import org.pe.nextcar.verification.domain.services.DniVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.pe.nextcar.verification.domain.services.DniVerificationService;
@Service
public class VerificationContextFacade {
    private final DniVerificationService dniVerificationService;

    public VerificationContextFacade(DniVerificationService dniVerificationService) {
        this.dniVerificationService = dniVerificationService;
    }

    /**
     * Lanza IllegalArgumentException con mensaje específico según el caso.
     * Retorna el DniResult si es válido y mayor de edad.
     */
    public DniResult validateAdultDni(String dni) {
        var result = dniVerificationService.verifyDni(dni);

        switch (result.status()) {
            case API_UNAVAILABLE ->
                    throw new IllegalArgumentException(
                            "El servicio de verificación de DNI no está disponible. " +
                                    "Intenta más tarde o contacta soporte.");
            case INVALID_DNI ->
                    throw new IllegalArgumentException(
                            "El DNI " + dni + " no fue encontrado en RENIEC.");
            case VALID -> {
                if (!dniVerificationService.isAdult(result)) {
                    throw new IllegalArgumentException(
                            "El titular del DNI es menor de edad. " +
                                    "Solo se aceptan solicitantes mayores de 18 años.");
                }
                return result;
            }
            default -> throw new IllegalArgumentException("Error en validación de DNI.");
        }
    }

    public boolean isValidAdultDni(String dni) {
        try {
            validateAdultDni(dni);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
