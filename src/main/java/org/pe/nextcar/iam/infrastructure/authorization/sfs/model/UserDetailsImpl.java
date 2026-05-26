package org.pe.nextcar.iam.infrastructure.authorization.sfs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.pe.nextcar.iam.domain.model.aggregates.User;

import java.util.Collection;
import java.util.stream.Collectors;

/** UserDetailsImpl type. */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
  private final String email;
  @JsonIgnore private final String password;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;
  private final Collection<? extends GrantedAuthority> authorities;

  /** Constructs a new UserDetailsImpl. */
  public UserDetailsImpl(
      String email, String password, Collection<? extends GrantedAuthority> authorities) {
    this.email = email;
    this.password = password;
    this.authorities = authorities;
    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
    this.enabled = true;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  /** Build. */
  public static UserDetailsImpl build(User user) {
    var authorities =
        user.getRoles().stream()
            .map(role -> role.getName().name())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    return new UserDetailsImpl(user.getEmail(), user.getPassword(), authorities);
  }
}
