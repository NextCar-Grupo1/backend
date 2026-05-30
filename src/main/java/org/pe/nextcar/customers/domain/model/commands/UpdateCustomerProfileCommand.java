package org.pe.nextcar.customers.domain.model.commands;

import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
public record UpdateCustomerProfileCommand(
        Long           customerId,
        String         address,
        String         district,
        String         city,
        EmploymentType employmentType,
        String         occupation,
        String         employer,
        double         monthlyIncome
) {}