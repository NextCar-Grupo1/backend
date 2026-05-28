package org.pe.nextcar.iam.application.internal.queryservices;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.domain.model.aggregates.User;
import org.pe.nextcar.iam.domain.model.queries.GetAllUsersQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByEmailQuery;
import org.pe.nextcar.iam.domain.model.queries.GetUserByIdQuery;
import org.pe.nextcar.iam.domain.services.UserQueryService;
import org.pe.nextcar.iam.infrastructure.persistence.jpa.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

/** UserQueryServiceImpl type. */
@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {
  private final UserRepository userRepository;

  /** Constructs a new UserQueryServiceImpl. */
  public UserQueryServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> handle(GetAllUsersQuery query) {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> handle(GetUserByIdQuery query) {
    return userRepository.findById(query.userId());
  }

  @Override
  public Optional<User> handle(GetUserByEmailQuery query) {
    return userRepository.findByEmail(query.email());
  }
}
