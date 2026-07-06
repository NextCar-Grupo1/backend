package org.pe.nextcar.iam.application.internal.queryservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.queries.GetAllUsersQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByEmailQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByIdQuery;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryServiceImpl queryService;

    @Test
    void handleGetAllUsers_ShouldReturnAllUsers() {
        // Arrange
        User user = new User("juan@nextcar.pe", "hashed", "Juan", "Perez", "999888777");
        when(userRepository.findAll()).thenReturn(List.of(user));

        // Act
        List<User> result = queryService.handle(new GetAllUsersQuery());

        // Assert
        assertEquals(1, result.size());
        assertEquals("juan@nextcar.pe", result.get(0).getEmail());
    }

    @Test
    void handleGetById_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = new User("juan@nextcar.pe", "hashed", "Juan", "Perez", "999888777");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = queryService.handle(new GetUserByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void handleGetById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = queryService.handle(new GetUserByIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void handleGetByEmail_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = new User("juan@nextcar.pe", "hashed", "Juan", "Perez", "999888777");
        when(userRepository.findByEmail("juan@nextcar.pe")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = queryService.handle(new GetUserByEmailQuery("juan@nextcar.pe"));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());
    }

    @Test
    void handleGetByEmail_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(userRepository.findByEmail("missing@nextcar.pe")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = queryService.handle(new GetUserByEmailQuery("missing@nextcar.pe"));

        // Assert
        assertTrue(result.isEmpty());
    }
}
