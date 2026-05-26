package org.pe.nextcar.iam.interfaces.rest.resources;

import java.util.Set;

/** AuthenticatedUserResource value carrier. */
public record AuthenticatedUserResource(Long id, String email, String token, Set<String> roles) {}
