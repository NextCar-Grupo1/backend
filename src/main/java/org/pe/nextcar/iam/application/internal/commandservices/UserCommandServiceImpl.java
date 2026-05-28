package org.pe.nextcar.iam.application.internal.commandservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.pe.nextcar.iam.application.internal.outboundservices.captcha.CaptchaVerifierService;
import org.pe.nextcar.iam.application.internal.outboundservices.dni.DniVerificationService;
import org.pe.nextcar.iam.application.internal.outboundservices.hashing.HashingService;
import org.pe.nextcar.iam.application.internal.outboundservices.tokens.TokenService;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.commands.SeedAdminUserCommand;
import org.pe.nextcar.iam.domain.model.commands.SignInCommand;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.valueobjects.AuthenticatedUser;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.iam.domain.services.UserCommandService;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserCommandServiceImpl implements UserCommandService {
  private final UserRepository userRepository;
  private final HashingService hashingService;
  private final TokenService tokenService;
  private final RoleRepository roleRepository;
  private final CaptchaVerifierService captchaVerifier;
  private final DniVerificationService dniVerificationService;

  public UserCommandServiceImpl(
      UserRepository userRepository,
      HashingService hashingService,
      TokenService tokenService,
      RoleRepository roleRepository,
      CaptchaVerifierService captchaVerifier,
      DniVerificationService dniVerificationService) {
    this.userRepository = userRepository;
    this.hashingService = hashingService;
    this.tokenService = tokenService;
    this.roleRepository = roleRepository;
    this.captchaVerifier = captchaVerifier;
    this.dniVerificationService = dniVerificationService;
  }

  @Override
  public Optional<User> handle(SignUpCommand command) {
    if (command.captchaToken() != null && !command.captchaToken().isBlank()) {
      captchaVerifier.verify(command.captchaToken());
    }

    if (userRepository.existsByEmail(command.email())) {
      throw new IllegalArgumentException("El correo electrónico ya está registrado");
    }

    if (command.documentNumber() != null && !command.documentNumber().isBlank()) {
      var dniResult = dniVerificationService.verifyDni(command.documentNumber());
      if (!dniResult.valid() || !dniVerificationService.isAdult(dniResult)) {
        throw new IllegalArgumentException("El DNI no es válido o el titular es menor de edad");
      }
    }


    var defaultRole = roleRepository.findByName(Roles.ROLE_USER)
        .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
    var user = new User(
        command.email(),
        hashingService.encode(command.password()),
        command.firstName(),
        command.lastName(),
        command.phone(),
        List.of(defaultRole));
    userRepository.save(user);
    return userRepository.findByEmail(command.email());
  }

  @Override
  public void handle(SeedAdminUserCommand command) {
    if (userRepository.findByEmail("admin@nextcar.pe").isPresent()) return;
    var adminRole = roleRepository.findByName(Roles.ROLE_ADMIN)
        .orElseThrow(() -> new RuntimeException("Admin role not seeded"));
    var admin = new User(
        "admin@nextcar.pe",
        hashingService.encode("Admin123!"),
        "Admin",
        "NextCar",
        null,
        List.of(adminRole));
    userRepository.save(admin);
  }

  @Override
  public Optional<AuthenticatedUser> handle(SignInCommand command) {
    var user = userRepository.findByEmail(command.email())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (!hashingService.matches(command.password(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid password");
    }
    var token = tokenService.generateToken(user.getEmail());
    return Optional.of(new AuthenticatedUser(user, token));
  }
}
