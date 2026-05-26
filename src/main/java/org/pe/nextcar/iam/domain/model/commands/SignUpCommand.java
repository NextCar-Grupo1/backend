package org.pe.nextcar.iam.domain.model.commands;

import org.pe.nextcar.iam.domain.model.entities.Role;

import java.util.List;

/** SignUpCommand value carrier. */
public record SignUpCommand(
    String email,
    String password,
    String firstName,
    String lastName,
    String phone,
    List<Role> roles) {}
