package org.pe.nextcar.iam.infrastructure.hashing.bcrypt;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.pe.nextcar.iam.application.internal.outboundservices.hashing.HashingService;

/** BCryptHashingService contract. */
public interface BCryptHashingService extends HashingService, PasswordEncoder {}
