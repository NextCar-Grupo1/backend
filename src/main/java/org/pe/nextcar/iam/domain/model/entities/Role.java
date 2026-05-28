package org.pe.nextcar.iam.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.pe.nextcar.iam.domain.model.valueobjects.Roles;
import org.pe.nextcar.shared.domain.model.entities.AuditableModel;

import java.util.List;

@Entity
  public class Role extends AuditableModel {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true, length = 20)
  private Roles name;

  /** Constructs a new Role. */
  public Role() {}

  /** Constructs a new Role. */
  public Role(Roles name) {
    this.name = name;
  }

  public String getStringName() {
    return name.name();
  }

  public static Role getDefaultRole() {
    return new Role(Roles.ROLE_USER);
  }

  /** To role from name. */
  public static Role toRoleFromName(String name) {
    return new Role(Roles.valueOf(name));
  }

  /** Validate role set. */
  public static List<Role> validateRoleSet(List<Role> roles) {
    if (roles == null || roles.isEmpty()) {
      return List.of(getDefaultRole());
    }
    return roles;
  }
}
