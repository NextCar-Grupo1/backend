package org.pe.nextcar.iam.infrastructure.captcha;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CaptchaVerificationService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.min-score:0.5}")
    private double minScore;

    private final RestClient restClient =
            RestClient.builder().build();

    public boolean verify(String token) {
        return true;
    }

    private record RecaptchaResponse(
            boolean success,
            double score,
            String action,
            @JsonProperty("error-codes")
            java.util.List<String> errorCodes
    ) {}
}