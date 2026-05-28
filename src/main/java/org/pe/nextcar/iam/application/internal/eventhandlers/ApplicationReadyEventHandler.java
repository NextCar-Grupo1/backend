package org.pe.nextcar.iam.application.internal.eventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.pe.nextcar.iam.domain.model.commands.SeedAdminUserCommand;
import org.pe.nextcar.iam.domain.model.commands.SeedRolesCommand;
import org.pe.nextcar.iam.domain.services.RoleCommandService;
import org.pe.nextcar.iam.domain.services.UserCommandService;

import java.sql.Timestamp;

/** ApplicationReadyEventHandler type. */
@Service
public class ApplicationReadyEventHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ApplicationReadyEventHandler.class);

  private final RoleCommandService roleCommandService;
  private final UserCommandService userCommandService;

  /** Constructs a new ApplicationReadyEventHandler. */
  public ApplicationReadyEventHandler(
      RoleCommandService roleCommandService,
      UserCommandService userCommandService) {
    this.roleCommandService = roleCommandService;
    this.userCommandService = userCommandService;
  }

  /** On. */
  @EventListener
  public void on(ApplicationReadyEvent event) {
    var applicationName = event.getApplicationContext().getId();
    LOGGER.info(
        "Starting to verify if roles seeding is needed for {} at {}",
        applicationName,
        getCurrentTimestamp());
    var seedRolesCommand = new SeedRolesCommand();
    roleCommandService.handle(seedRolesCommand);
    LOGGER.info(
        "Roles seeding verification finished for {} at {}", applicationName, getCurrentTimestamp());

    LOGGER.info(
        "Starting to verify if admin user seeding is needed for {} at {}",
        applicationName,
        getCurrentTimestamp());
    var seedAdminUserCommand = new SeedAdminUserCommand();
    userCommandService.handle(seedAdminUserCommand);
    LOGGER.info(
        "Admin user seeding verification finished for {} at {}",
        applicationName,
        getCurrentTimestamp());
  }

  private Timestamp getCurrentTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }
}
