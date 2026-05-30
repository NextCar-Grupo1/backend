package org.pe.nextcar.customers.domain.services;

import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.commands.*;
import java.util.Optional;
public interface CustomerCommandService {
    Optional<Customer> handle(CreateCustomerProfileCommand command);
    Optional<Customer> handle(UpdateCustomerProfileCommand command);
}