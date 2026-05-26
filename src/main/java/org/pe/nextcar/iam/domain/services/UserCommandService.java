package org.pe.nextcar.iam.domain.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.commands.SignInCommand;
import org.pe.nextcar.iam.domain.model.commands.SignUpCommand;

import java.util.Optional;

/** UserCommandService contract. */
public interface UserCommandService {
  /** Handle. */
  Optional<User> handle(SignUpCommand command);

  /** Handle. */
  Optional<ImmutablePair<User, String>> handle(SignInCommand command);
}
