package org.pe.nextcar.iam.interfaces.rest.resources;

import java.util.List;

public record SignUpResource(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        String documentNumber,   // DNI peruano (8 dígitos)
        String captchaToken,     // token de reCAPTCHA v3
        List<String> roles
) {}