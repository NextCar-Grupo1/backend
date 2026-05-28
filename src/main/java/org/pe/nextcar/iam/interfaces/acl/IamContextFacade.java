package org.pe.nextcar.iam.interfaces.acl;

import org.pe.nextcar.iam.interfaces.rest.resources.UserResource;

import java.util.List;
import java.util.Optional;

public interface IamContextFacade {
  Optional<Long> createUser(String email, String password, String firstName, String lastName, String phone);
  Optional<Long> createUser(String email, String password, String firstName, String lastName,
                            String phone, String documentNumber, String captchaToken, List<String> roleNames);
  Optional<UserResource> fetchUserById(Long userId);
  Optional<Long> fetchUserIdByEmail(String email);
  boolean existsUserByEmailAndIdIsNot(String email, Long id);
  boolean existsUserById(Long id);
  Optional<String> fetchEmailByUserId(Long userId);
  boolean existsUserByRole(Long userId, String roleName);
}
