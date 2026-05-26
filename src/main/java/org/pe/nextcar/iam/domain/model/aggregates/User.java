package org.pe.nextcar.iam.domain.model.aggregates;

import  jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.pe.nextcar.iam.domain.model.entities.Role;
import org.pe.nextcar.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** User type. */
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

  @Getter
  @NotBlank
  @Column(unique = true)
  @Size(max = 254)
  private String email;

  @Getter
  @NotBlank
  @Size(max = 256)
  private String password;

  @Getter
  @NotBlank
  @Size(max = 60)
  private String firstName;

  @Getter
  @NotBlank
  @Size(max = 60)
  private String lastName;

  @Getter
  @Size(max = 20)
  private String phone;

  @Getter private boolean isActive;

  @Getter private boolean emailVerified;

  @Getter
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  /** Constructs a new User. */
  public User() {
    this.roles = new HashSet<>();
    this.isActive = true;
    this.emailVerified = false;
  }

  /** Constructs a new User. */
  public User(String email, String password, String firstName, String lastName, String phone) {
    this();
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phone = phone;
    this.roles = new HashSet<>();
  }

  /** Constructs a new User. */
  public User(
      String email,
      String password,
      String firstName,
      String lastName,
      String phone,
      List<Role> roles) {
    this(email, password, firstName, lastName, phone);
    addRoles(roles);
  }

  /** Add role. */
  public User addRole(Role role) {
    this.roles.add(role);
    return this;
  }

  /** Add roles. */
  public User addRoles(List<Role> roles) {
    var validatedRoleSet = Role.validateRoleSet(roles);
    this.roles.addAll(validatedRoleSet);
    return this;
  }

  /** Verify email. */
  public void verifyEmail() {
    this.emailVerified = true;
  }

  /** Deactivate user. */
  public void deactivateUser() {
    this.isActive = false;
  }

  /** Activate user. */
  public void activateUser() {
    this.isActive = true;
  }

  /** Update user info. */
  public void updateUserInfo(String firstName, String lastName, String phone) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.phone = phone;
  }
}
