package org.pe.nextcar.customers.domain.services;
import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.queries.*;
import java.util.Optional;
public interface CustomerQueryService {
    Optional<Customer> handle(GetCustomerByUserIdQuery query);
    Optional<Customer> handle(GetCustomerByIdQuery query);
}