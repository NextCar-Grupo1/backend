package org.pe.nextcar.iam.infrastructure.captcha.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.pe.nextcar.iam.application.internal.outboundservices.captcha.CaptchaVerifierService;

@Service
public class GoogleRecaptchaVerifierServiceImpl implements CaptchaVerifierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleRecaptchaVerifierServiceImpl.class);

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.min-score:0.5}")
    private double minScore;

    private final RestClient restClient = RestClient.builder().build();

    @Override
    public void verify(String token) {
        if (secretKey.isBlank()) {
            LOGGER.warn("CAPTCHA disabled: recaptcha.secret-key not configured");
            return;
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("CAPTCHA token is required");
        }
        var response = restClient.get()
                .uri("https://www.google.com/recaptcha/api/siteverify?secret={secret}&response={token}", secretKey, token)
                .retrieve()
                .body(RecaptchaResponse.class);
        if (response == null || !response.success() || response.score() < minScore) {
            LOGGER.warn("CAPTCHA verification failed - score: {}", response != null ? response.score() : "null");
            throw new IllegalArgumentException("CAPTCHA verification failed");
        }
        LOGGER.debug("CAPTCHA verification passed - score: {}", response.score());
    }

    private record RecaptchaResponse(boolean success, double score, String action) {}
}
