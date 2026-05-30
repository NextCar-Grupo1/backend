package org.pe.nextcar.customers.interfaces.rest.resources;

import jakarta.validation.constraints.*;
import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
public record CreateCustomerProfileResource(
        @NotBlank @Size(min=8, max=8) String documentNumber,
        @NotBlank String address,
        @NotBlank String district,
        @NotBlank String city,
        @NotNull  EmploymentType employmentType,
        @NotBlank String occupation,
        String         employer,
        @Positive double monthlyIncome
) {}