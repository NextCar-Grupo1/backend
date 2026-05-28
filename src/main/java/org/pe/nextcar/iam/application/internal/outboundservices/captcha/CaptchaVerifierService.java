package org.pe.nextcar.iam.application.internal.outboundservices.captcha;

public interface CaptchaVerifierService {
    void verify(String token);
}
