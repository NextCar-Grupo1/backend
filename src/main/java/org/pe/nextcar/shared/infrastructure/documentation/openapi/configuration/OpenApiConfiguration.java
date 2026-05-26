package org.pe.nextcar.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
  @Bean
  public OpenAPI learningPlatformOpenApi() {
    // General configuration
    var openApi = new OpenAPI();
    openApi
        .info(new Info()
            .title("Nextcard  API")
            .description(" REST API documentation.")
            .version("v1.0.0")
            .license(new License().name("Apache 2.0")
                .url("https://springdoc.org")))
        .externalDocs(new ExternalDocumentation()
            .description("Retail Store Platform Documentation")
            .url("https://github.com/upc-is-si732"));



    //Add security scheme
    final String securitySchemeName = "bearerAuth";

    openApi.addSecurityItem(new SecurityRequirement()
            .addList(securitySchemeName))
        .components(new Components()
            .addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));

    // Return OpenAPI configuration object with all the settings
    return openApi;
  }
  private ExternalDocumentation externalDocs() {
    return new ExternalDocumentation()
            .description("Bodymatch External Documentation")
            .url("https://github.com/G3-FundamentosArqui-7944/backend");
  }
}