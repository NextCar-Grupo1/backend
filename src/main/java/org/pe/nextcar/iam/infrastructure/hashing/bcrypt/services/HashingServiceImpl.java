package org.pe.nextcar.iam.infrastructure.hashing.bcrypt.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.infrastructure.hashing.bcrypt.BCryptHashingService;

/** HashingServiceImpl type. */
@Service
public class HashingServiceImpl implements BCryptHashingService {
  private final BCryptPasswordEncoder passwordEncoder;

  /** Constructs a new HashingServiceImpl. */
  public HashingServiceImpl() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
