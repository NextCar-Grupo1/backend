package org.pe.nextcar.iam.application.internal.commandservices;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import  org.pe.nextcar.iam.application.internal.outboundservices.hashing.HashingService;
import org.pe.nextcar.iam.application.internal.outboundservices.tokens.TokenService;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.commands.SignInCommand;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.iam.domain.services.UserCommandService;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.Optional;

/** UserCommandServiceImpl type. */
@Service
public class UserCommandServiceImpl implements UserCommandService {
  private final UserRepository userRepository;
  private final HashingService hashingService;
  private final TokenService tokenService;
  private final RoleRepository roleRepository;

  /** Constructs a new UserCommandServiceImpl. */
  public UserCommandServiceImpl(
      UserRepository userRepository,
      HashingService hashingService,
      TokenService tokenService,
      RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.hashingService = hashingService;
    this.tokenService = tokenService;
    this.roleRepository = roleRepository;
  }

  @Override
  public Optional<User> handle(SignUpCommand command) {
    if (userRepository.existsByEmail(command.email())) {
      throw new IllegalArgumentException("Email already exists");
    }

    var roles = command.roles();
    if (roles.isEmpty()) {
      var role = roleRepository.findByName(Roles.ROLE_USER);
      if (role.isPresent()) {
        roles.add(role.get());
      }
    } else {
      roles =
          roles.stream()
              .map(
                  role ->
                      roleRepository
                          .findByName(role.getName())
                          .orElseThrow(() -> new IllegalArgumentException("Role not found")))
              .toList();
    }
    var user =
        new User(
            command.email(),
            hashingService.encode(command.password()),
            command.firstName(),
            command.lastName(),
            command.phone(),
            roles);
    userRepository.save(user);
    return userRepository.findByEmail(command.email());
  }

  @Override
  public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
    var user =
        userRepository
            .findByEmail(command.email())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (!hashingService.matches(command.password(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid password");
    }
    var token = tokenService.generateToken(user.getEmail());
    return Optional.of(new ImmutablePair<>(user, token));
  }
}
