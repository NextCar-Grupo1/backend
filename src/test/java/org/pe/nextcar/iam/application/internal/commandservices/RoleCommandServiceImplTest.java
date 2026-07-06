package org.pe.nextcar.iam.application.internal.commandservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.iam.domain.model.commands.SeedRolesCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleCommandServiceImplTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleCommandServiceImpl commandService;

    @Test
    void handle_ShouldSaveAllMissingRoles_WhenNoneExist() {
        // Arrange
        when(repository.existsByName(any(Roles.class))).thenReturn(false);

        // Act
        commandService.handle(new SeedRolesCommand());

        // Assert
        verify(repository, times(Roles.values().length)).save(any(Role.class));
    }

    @Test
    void handle_ShouldNotSaveAnyRole_WhenAllAlreadyExist() {
        // Arrange
        when(repository.existsByName(any(Roles.class))).thenReturn(true);

        // Act
        commandService.handle(new SeedRolesCommand());

        // Assert
        verify(repository, never()).save(any(Role.class));
    }

    @Test
    void handle_ShouldSaveOnlyMissingRole_WhenSomeAlreadyExist() {
        // Arrange
        when(repository.existsByName(Roles.ROLE_ADMIN)).thenReturn(true);
        when(repository.existsByName(Roles.ROLE_USER)).thenReturn(false);

        // Act
        commandService.handle(new SeedRolesCommand());

        // Assert
        verify(repository, times(1)).save(any(Role.class));
    }
}
