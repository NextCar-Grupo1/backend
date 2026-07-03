package org.pe.nextcar.iam.application.internal.queryservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.queries.GetAllRolesQuery;
import org.pe.nextcar.iam.domain.model.queries.GetRoleByIdQuery;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleQueryServiceImplTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleQueryServiceImpl queryService;

    @Test
    void handleGetAll_ShouldReturnAllRoles() {
        // Arrange
        Role userRole = new Role(Roles.ROLE_USER);
        Role adminRole = new Role(Roles.ROLE_ADMIN);
        when(repository.findAll()).thenReturn(List.of(userRole, adminRole));

        // Act
        List<Role> result = queryService.handle(new GetAllRolesQuery());

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void handleGetById_ShouldReturnRole_WhenExists() {
        // Arrange
        Role userRole = new Role(Roles.ROLE_USER);
        when(repository.findById(1L)).thenReturn(Optional.of(userRole));

        // Act
        Optional<Role> result = queryService.handle(new GetRoleByIdQuery(1L));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Roles.ROLE_USER, result.get().getName());
    }

    @Test
    void handleGetById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Role> result = queryService.handle(new GetRoleByIdQuery(99L));

        // Assert
        assertTrue(result.isEmpty());
    }
}
