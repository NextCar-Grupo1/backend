package org.pe.nextcar.shared.infrastructure.environment.dotenv.configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class DotEnvConfig {
    public static void load() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    }
}