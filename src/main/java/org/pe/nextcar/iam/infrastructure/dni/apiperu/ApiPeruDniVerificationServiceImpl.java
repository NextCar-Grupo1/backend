package org.pe.nextcar.iam.infrastructure.dni.apiperu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.pe.nextcar.iam.application.internal.outboundservices.dni.DniVerificationService;
import org.pe.nextcar.iam.domain.model.valueobjects.DniResult;
import org.pe.nextcar.iam.domain.model.valueobjects.DniVerificationStatus;

@Service
public class ApiPeruDniVerificationServiceImpl implements DniVerificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiPeruDniVerificationServiceImpl.class);

    private final RestClient restClient = RestClient.builder().build();

    @Value("${apiperu.token:}")
    private String apiToken;

    @Value("${apiperu.base-url:https://apiperu.dev/api}")
    private String baseUrl;

    @Value("${apiperu.enabled:true}")
    private boolean enabled;

    @Override
    public DniResult verifyDni(String dni) {
        if (!enabled) {
            LOGGER.warn("API Peru validation disabled (apiperu.enabled=false)");
            return bypassResult(dni);
        }
        if (apiToken == null || apiToken.isBlank()) {
            LOGGER.error("apiperu.token is not configured");
            return unavailableResult(dni);
        }
        try {
            var response = restClient.post()
                    .uri(baseUrl + "/dni")
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Content-Type", "application/json")
                    .body("{\"dni\":\"" + dni + "\"}")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiPeruResponse<ApiPeruDniData>>() {});

            LOGGER.info("API Peru response: {}", response);
            if (response == null || !response.success() || response.data() == null) {
                LOGGER.warn("Invalid API Peru response for DNI {}", dni);
                return new DniResult(dni, null, null, null, null, false, DniVerificationStatus.INVALID_DNI);
            }
            var data = response.data();
            return new DniResult(data.dni(), data.nombres(),
                data.apellidoPaterno(), data.apellidoMaterno(),
                null, true, DniVerificationStatus.VALID);
        } catch (Exception e) {
            LOGGER.error("API Peru error for DNI {} -> {}", dni, e.getMessage());
            return unavailableResult(dni);
        }
    }

    @Override
    public boolean isAdult(DniResult result) {
        return result.valid();
    }

    private DniResult bypassResult(String dni) {
        return new DniResult(dni, null, null, null, null, true, DniVerificationStatus.VALID);
    }

    private DniResult unavailableResult(String dni) {
        return new DniResult(dni, null, null, null, null, false, DniVerificationStatus.API_UNAVAILABLE);
    }
}
