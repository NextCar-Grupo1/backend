package org.pe.nextcar.iam.domain.model.commands;

public record SignUpCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        String documentNumber,
        String captchaToken
) {}
