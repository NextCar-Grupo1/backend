package org.pe.nextcar.iam.domain.model.commands;

import org.pe.nextcar.iam.domain.model.entities.Role;

import java.util.List;


public record SignUpCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        String documentNumber,
        String captchaToken,
        List<Role> roles
) {}
