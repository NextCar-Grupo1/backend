package org.pe.nextcar.iam.application.internal.outboundservices.hashing;

/** HashingService contract. */
public interface HashingService {
  /** Encode. */
  String encode(CharSequence rawPassword);

  /** Matches. */
  boolean matches(CharSequence rawPassword, String encodedPassword);
}
