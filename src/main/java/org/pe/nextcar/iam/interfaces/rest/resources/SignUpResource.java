package org.pe.nextcar.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SignUpResource(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 256) String password,
        @NotBlank @Size(max = 60) String firstName,
        @NotBlank @Size(max = 60) String lastName,
        @Size(max = 20) String phone,
        String documentNumber,
        String captchaToken
) {}