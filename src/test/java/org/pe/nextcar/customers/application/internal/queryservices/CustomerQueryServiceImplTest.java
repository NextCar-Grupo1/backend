package org.pe.nextcar.customers.application.internal.queryservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.queries.GetCustomerByIdQuery;
import org.pe.nextcar.customers.domain.model.queries.GetCustomerByUserIdQuery;
import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
import org.pe.nextcar.customers.infrastructure.persistence.jpa.repositories.CustomerRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerQueryServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerQueryServiceImpl queryService;

    @Test
    void handleGetByUserId_ShouldReturnCustomer_WhenExists() {
        // Arrange
        Customer customer = new Customer(1L, "70293844", "Address", "District",
                "Lima", EmploymentType.DEPENDENT, "Engineer", "Tech Corp", 5500.0);
        when(repository.findByUserId(1L)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = queryService.handle(new GetCustomerByUserIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
    }

    @Test
    void handleGetByUserId_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = queryService.handle(new GetCustomerByUserIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void handleGetById_ShouldReturnCustomer_WhenExists() {
        // Arrange
        Customer customer = new Customer(1L, "70293844", "Address", "District",
                "Lima", EmploymentType.DEPENDENT, "Engineer", "Tech Corp", 5500.0);
        when(repository.findById(10L)).thenReturn(Optional.of(customer));

        // Act
        Optional<Customer> result = queryService.handle(new GetCustomerByIdQuery(10L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("70293844", result.get().getDocumentNumber());
    }

    @Test
    void handleGetById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = queryService.handle(new GetCustomerByIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
    }
}
