package org.pe.nextcar.iam.domain.model.valueobjects;

import org.pe.nextcar.iam.domain.model.aggregates.User;

public record AuthenticatedUser(User user, String token) {}
