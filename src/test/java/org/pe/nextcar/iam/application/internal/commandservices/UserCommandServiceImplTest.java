package org.pe.nextcar.iam.application.internal.commandservices;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pe.nextcar.iam.application.internal.outboundservices.captcha.CaptchaVerifierService;
import org.pe.nextcar.iam.application.internal.outboundservices.dni.DniVerificationService;
import org.pe.nextcar.iam.application.internal.outboundservices.hashing.HashingService;
import org.pe.nextcar.iam.application.internal.outboundservices.tokens.TokenService;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.commands.SeedAdminUserCommand;
import org.pe.nextcar.iam.domain.model.commands.SignInCommand;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.domain.model.valueobjects.AuthenticatedUser;
import org.pe.nextcar.iam.domain.model.valueobjects.DniResult;
import org.pe.nextcar.iam.domain.model.valueobjects.DniVerificationStatus;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private HashingService hashingService;
    @Mock private TokenService tokenService;
    @Mock private RoleRepository roleRepository;
    @Mock private CaptchaVerifierService captchaVerifier;
    @Mock private DniVerificationService dniVerificationService;

    @InjectMocks
    private UserCommandServiceImpl commandService;

    @Test
    void handleSignUp_ShouldCreateUser_WhenEmailIsFreeAndNoDniProvided() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "juan@nextcar.pe", "rawPassword", "Juan", "Perez", "999888777", null, null);
        Role defaultRole = new Role(Roles.ROLE_USER);
        User savedUser = new User("juan@nextcar.pe", "hashed", "Juan", "Perez", "999888777", List.of(defaultRole));

        when(userRepository.existsByEmail("juan@nextcar.pe")).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_USER)).thenReturn(Optional.of(defaultRole));
        when(hashingService.encode("rawPassword")).thenReturn("hashed");
        when(userRepository.findByEmail("juan@nextcar.pe")).thenReturn(Optional.of(savedUser));

        // Act
        Optional<User> result = commandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("juan@nextcar.pe", result.get().getEmail());
        verify(userRepository).save(any(User.class));
        verifyNoInteractions(captchaVerifier);
        verifyNoInteractions(dniVerificationService);
    }

    @Test
    void handleSignUp_ShouldThrow_WhenEmailAlreadyRegistered() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "juan@nextcar.pe", "rawPassword", "Juan", "Perez", "999888777", null, null);
        when(userRepository.existsByEmail("juan@nextcar.pe")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(userRepository, never()).save(any());
    }

    @Test
    void handleSignUp_ShouldVerifyCaptcha_WhenTokenProvided() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "juan@nextcar.pe", "rawPassword", "Juan", "Perez", "999888777", null, "captcha-token");
        Role defaultRole = new Role(Roles.ROLE_USER);
        when(userRepository.existsByEmail("juan@nextcar.pe")).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_USER)).thenReturn(Optional.of(defaultRole));
        when(hashingService.encode("rawPassword")).thenReturn("hashed");
        when(userRepository.findByEmail("juan@nextcar.pe")).thenReturn(Optional.empty());

        // Act
        commandService.handle(command);

        // Assert
        verify(captchaVerifier).verify("captcha-token");
    }

    @Test
    void handleSignUp_ShouldThrow_WhenDniIsInvalid() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "juan@nextcar.pe", "rawPassword", "Juan", "Perez", "999888777", "12345678", null);
        DniResult invalidDni = new DniResult("12345678", null, null, null, null, false, DniVerificationStatus.INVALID_DNI);
        when(userRepository.existsByEmail("juan@nextcar.pe")).thenReturn(false);
        when(dniVerificationService.verifyDni("12345678")).thenReturn(invalidDni);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(userRepository, never()).save(any());
    }

    @Test
    void handleSignUp_ShouldCreateUser_WhenDniIsValidAndAdult() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "juan@nextcar.pe", "rawPassword", "Juan", "Perez", "999888777", "12345678", null);
        DniResult validDni = new DniResult("12345678", "Juan", "Perez", "Gomez", null, true, DniVerificationStatus.VALID);
        Role defaultRole = new Role(Roles.ROLE_USER);
        when(userRepository.existsByEmail("juan@nextcar.pe")).thenReturn(false);
        when(dniVerificationService.verifyDni("12345678")).thenReturn(validDni);
        when(dniVerificationService.isAdult(validDni)).thenReturn(true);
        when(roleRepository.findByName(Roles.ROLE_USER)).thenReturn(Optional.of(defaultRole));
        when(hashingService.encode("rawPassword")).thenReturn("hashed");
        when(userRepository.findByEmail("juan@nextcar.pe")).thenReturn(Optional.empty());

        // Act
        commandService.handle(command);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void handleSignIn_ShouldReturnAuthenticatedUser_WhenCredentialsAreValid() {
        // Arrange
        SignInCommand command = new SignInCommand("juan@nextcar.pe", "rawPassword");
        User user = new User("juan@nextcar.pe", "hashed", "Juan", "Perez", "999888777");
        when(userRepository.findByEmail("juan@nextcar.pe")).thenReturn(Optional.of(user));
        when(hashingService.matches("rawPassword", "hashed")).thenReturn(true);
        when(tokenService.generateToken("juan@nextcar.pe")).thenReturn("jwt-token");

        // Act
        Optional<AuthenticatedUser> result = commandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("jwt-token", result.get().token());
        assertEquals(user, result.get().user());
    }

    @Test
    void handleSignIn_ShouldThrow_WhenUserNotFound() {
        // Arrange
        SignInCommand command = new SignInCommand("missing@nextcar.pe", "rawPassword");
        when(userRepository.findByEmail("missing@nextcar.pe")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
    }

    @Test
    void handleSignIn_ShouldThrow_WhenPasswordDoesNotMatch() {
        // Arrange
        SignInCommand command = new SignInCommand("juan@nextcar.pe", "wrongPassword");
        User user = new User("juan@nextcar.pe", "hashed", "Juan", "Perez", "999888777");
        when(userRepository.findByEmail("juan@nextcar.pe")).thenReturn(Optional.of(user));
        when(hashingService.matches("wrongPassword", "hashed")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void handleSeedAdminUser_ShouldCreateAdmin_WhenNotAlreadySeeded() {
        // Arrange
        Role adminRole = new Role(Roles.ROLE_ADMIN);
        when(userRepository.findByEmail("admin@nextcar.pe")).thenReturn(Optional.empty());
        when(roleRepository.findByName(Roles.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(hashingService.encode("Admin123!")).thenReturn("hashedAdminPass");

        // Act
        commandService.handle(new SeedAdminUserCommand());

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void handleSeedAdminUser_ShouldDoNothing_WhenAdminAlreadyExists() {
        // Arrange
        User existingAdmin = new User("admin@nextcar.pe", "hashed", "Admin", "NextCar", null);
        when(userRepository.findByEmail("admin@nextcar.pe")).thenReturn(Optional.of(existingAdmin));

        // Act
        commandService.handle(new SeedAdminUserCommand());

        // Assert
        verify(userRepository, never()).save(any());
        verifyNoInteractions(roleRepository);
    }
}
