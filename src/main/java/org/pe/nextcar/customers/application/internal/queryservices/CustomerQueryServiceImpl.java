package org.pe.nextcar.customers.application.internal.queryservices;

import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.queries.*;
import org.pe.nextcar.customers.domain.services.CustomerQueryService;
import org.pe.nextcar.customers.infrastructure.persistence.jpa.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerRepository repository;

    public CustomerQueryServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Customer> handle(GetCustomerByUserIdQuery query) {
        return repository.findByUserId(query.userId());
    }

    @Override
    public Optional<Customer> handle(GetCustomerByIdQuery query) {
        return repository.findById(query.customerId());
    }
}