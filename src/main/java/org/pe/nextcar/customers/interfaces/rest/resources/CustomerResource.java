package org.pe.nextcar.customers.interfaces.rest.resources;

import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
public record CustomerResource(
        Long           id,
        Long           userId,
        String         documentNumber,
        String         address,
        String         district,
        String         city,
        EmploymentType employmentType,
        String         employmentTypeDisplayName,
        String         occupation,
        String         employer,
        double         monthlyIncome,
        boolean        profileComplete
) {}