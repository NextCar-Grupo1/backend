package org.pe.nextcar.verification.infrastructure.external.reniec;

import org.pe.nextcar.verification.domain.model.valueobjects.DniResult;
import org.pe.nextcar.verification.domain.model.valueobjects.DniVerificationStatus;
import org.pe.nextcar.verification.domain.services.DniVerificationService;
import org.pe.nextcar.verification.infrastructure.external.reniec.dto.ReniecApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
public class ReniecApiServiceImpl implements DniVerificationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ReniecApiServiceImpl.class);

    private static final DateTimeFormatter RENIEC_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RestClient restClient =
            RestClient.builder().build();

    @Value("${reniec.api.token:}")
    private String apiToken;

    @Value("${reniec.api.base-url:https://apiperu.dev/api}")
    private String baseUrl;

    @Value("${reniec.api.enabled:true}")
    private boolean enabled;

    @Override
    public DniResult verifyDni(String dni) {

        // Desactivado por configuración
        if (!enabled) {

            LOGGER.warn(
                    "RENIEC validation disabled (reniec.api.enabled=false)"
            );

            return bypassResult(dni);
        }

        // Token no configurado
        if (apiToken == null || apiToken.isBlank()) {

            LOGGER.error(
                    "reniec.api.token is not configured"
            );

            return unavailableResult(dni);
        }

        try {

            var response = restClient.get()
                    .uri(baseUrl + "/dni?numero=" + dni)
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(ReniecApiResponse.class);

            LOGGER.info("RENIEC response: {}", response);

            // Respuesta inválida
            if (response == null
                    || response.nombres() == null
                    || response.fechaNacimiento() == null) {

                LOGGER.warn(
                        "Invalid RENIEC response for DNI {}",
                        dni
                );

                return new DniResult(
                        dni,
                        null,
                        null,
                        null,
                        null,
                        false,
                        DniVerificationStatus.INVALID_DNI
                );
            }

            var birthDate = LocalDate.parse(
                    response.fechaNacimiento(),
                    RENIEC_FORMAT
            );

            return new DniResult(
                    response.dni(),
                    response.nombres(),
                    response.apellidoPaterno(),
                    response.apellidoMaterno(),
                    birthDate,
                    true,
                    DniVerificationStatus.VALID
            );

        } catch (Exception e) {

            LOGGER.error(
                    "RENIEC API error for DNI {} -> {}",
                    dni,
                    e.getMessage()
            );

            return unavailableResult(dni);
        }
    }

    @Override
    public boolean isAdult(DniResult result) {

        if (!result.valid()
                || result.fechaNacimiento() == null) {
            return false;
        }

        return Period.between(
                result.fechaNacimiento(),
                LocalDate.now()
        ).getYears() >= 18;
    }

    private DniResult bypassResult(String dni) {

        return new DniResult(
                dni,
                null,
                null,
                null,
                null,
                true,
                DniVerificationStatus.VALID
        );
    }

    private DniResult unavailableResult(String dni) {

        return new DniResult(
                dni,
                null,
                null,
                null,
                null,
                false,
                DniVerificationStatus.API_UNAVAILABLE
        );
    }
}