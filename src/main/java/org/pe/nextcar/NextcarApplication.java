package org.pe.nextcar;

import io.github.cdimascio.dotenv.Dotenv;
import org.pe.nextcar.shared.infrastructure.environment.dotenv.configuration.DotEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NextcarApplication {

	public static void main(String[] args) {
		SpringApplication.run(NextcarApplication.class, args);
	}

}
