package org.pe.nextcar.iam.domain.services;

import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.queries.GetAllUsersQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByEmailQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByIdQuery;

import java.util.List;
import java.util.Optional;

/** UserQueryService contract. */
public interface UserQueryService {
  /** Handle. */
  List<User> handle(GetAllUsersQuery query);

  /** Handle. */
  Optional<User> handle(GetUserByIdQuery query);

  /** Handle. */
  Optional<User> handle(GetUserByEmailQuery query);
}
