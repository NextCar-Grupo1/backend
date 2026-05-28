package org.pe.nextcar.iam.domain.services;

import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.commands.SeedAdminUserCommand;
import org.pe.nextcar.iam.domain.model.commands.SignInCommand;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;
import org.pe.nextcar.iam.domain.model.valueobjects.AuthenticatedUser;

import java.util.Optional;

public interface UserCommandService {
  Optional<User> handle(SignUpCommand command);
  Optional<AuthenticatedUser> handle(SignInCommand command);
  void handle(SeedAdminUserCommand command);
}
