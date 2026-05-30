package org.pe.nextcar.customers.application.internal.commandservices;
import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.commands.*;
import org.pe.nextcar.customers.domain.services.CustomerCommandService;
import org.pe.nextcar.customers.infrastructure.persistence.jpa.repositories.CustomerRepository;
import org.pe.nextcar.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository repository;
    private final IamContextFacade   iamFacade;

    public CustomerCommandServiceImpl(CustomerRepository repository, IamContextFacade iamFacade) {
        this.repository = repository;
        this.iamFacade  = iamFacade;
    }

    @Override
    public Optional<Customer> handle(CreateCustomerProfileCommand cmd) {
        // Verify the user exists in IAM before creating the profile
        if (!iamFacade.existsUserById(cmd.userId())) {
            throw new IllegalArgumentException("Usuario con id " + cmd.userId() + " no existe.");
        }
        if (repository.existsByUserId(cmd.userId())) {
            throw new IllegalArgumentException("El usuario ya tiene un perfil de cliente.");
        }
        var customer = new Customer(
                cmd.userId(), cmd.documentNumber(), cmd.address(),
                cmd.district(), cmd.city(), cmd.employmentType(),
                cmd.occupation(), cmd.employer(), cmd.monthlyIncome()
        );
        return Optional.of(repository.save(customer));
    }

    @Override
    public Optional<Customer> handle(UpdateCustomerProfileCommand cmd) {
        var customer = repository.findById(cmd.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de cliente no encontrado."));
        customer.updateProfile(cmd.address(), cmd.district(), cmd.city(),
                cmd.employmentType(), cmd.occupation(), cmd.employer(), cmd.monthlyIncome());
        return Optional.of(repository.save(customer));
    }
}