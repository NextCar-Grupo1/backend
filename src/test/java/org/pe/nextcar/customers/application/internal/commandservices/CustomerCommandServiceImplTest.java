package org.pe.nextcar.customers.application.internal.commandservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.customers.domain.model.aggregates.Customer;
import org.pe.nextcar.customers.domain.model.commands.CreateCustomerProfileCommand;
import org.pe.nextcar.customers.domain.model.commands.UpdateCustomerProfileCommand;
import org.pe.nextcar.customers.domain.model.valueobjects.EmploymentType;
import org.pe.nextcar.customers.infrastructure.persistence.jpa.repositories.CustomerRepository;
import org.pe.nextcar.iam.interfaces.acl.IamContextFacade;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerCommandServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private IamContextFacade iamFacade;

    @InjectMocks
    private CustomerCommandServiceImpl commandService;

    @Test
    void handleCreate_ShouldCreateProfile_WhenUserExistsAndHasNoProfile() {
        // Arrange
        CreateCustomerProfileCommand command = new CreateCustomerProfileCommand(
                1L, "70293844", "Av. Javier Prado", "San Isidro", "Lima",
                EmploymentType.DEPENDENT, "Engineer", "Tech Corp", 5500.0);
        when(iamFacade.existsUserById(1L)).thenReturn(true);
        when(repository.existsByUserId(1L)).thenReturn(false);
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Customer> result = commandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("70293844", result.get().getDocumentNumber());
        verify(repository).save(any(Customer.class));
    }

    @Test
    void handleCreate_ShouldThrow_WhenUserDoesNotExistInIam() {
        // Arrange
        CreateCustomerProfileCommand command = new CreateCustomerProfileCommand(
                1L, "70293844", "Av. Javier Prado", "San Isidro", "Lima",
                EmploymentType.DEPENDENT, "Engineer", "Tech Corp", 5500.0);
        when(iamFacade.existsUserById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(repository, never()).save(any());
    }

    @Test
    void handleCreate_ShouldThrow_WhenCustomerProfileAlreadyExists() {
        // Arrange
        CreateCustomerProfileCommand command = new CreateCustomerProfileCommand(
                1L, "70293844", "Av. Javier Prado", "San Isidro", "Lima",
                EmploymentType.DEPENDENT, "Engineer", "Tech Corp", 5500.0);
        when(iamFacade.existsUserById(1L)).thenReturn(true);
        when(repository.existsByUserId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(repository, never()).save(any());
    }

    @Test
    void handleUpdate_ShouldUpdateProfile_WhenFound() {
        // Arrange
        Customer existing = new Customer(1L, "70293844", "Old Address", "Old District",
                "Lima", EmploymentType.DEPENDENT, "Engineer", "Tech Corp", 5500.0);
        UpdateCustomerProfileCommand command = new UpdateCustomerProfileCommand(
                10L, "New Address", "Miraflores", "Lima",
                EmploymentType.INDEPENDENT, "Consultant", "", 7200.0);
        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Customer> result = commandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Address", result.get().getAddress());
        assertEquals(EmploymentType.INDEPENDENT, result.get().getEmploymentType());
        assertEquals(7200.0, result.get().getMonthlyIncome());
    }

    @Test
    void handleUpdate_ShouldThrow_WhenCustomerNotFound() {
        // Arrange
        UpdateCustomerProfileCommand command = new UpdateCustomerProfileCommand(
                99L, "New Address", "Miraflores", "Lima",
                EmploymentType.INDEPENDENT, "Consultant", "", 7200.0);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(repository, never()).save(any());
    }
}
