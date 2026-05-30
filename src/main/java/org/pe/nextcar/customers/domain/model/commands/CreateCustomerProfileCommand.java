package org.pe.nextcar.customers.domain.model.commands;

import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
public record CreateCustomerProfileCommand(
        Long           userId,
        String         documentNumber,
        String         address,
        String         district,
        String         city,
        EmploymentType employmentType,
        String         occupation,
        String         employer,
        double         monthlyIncome
) {}