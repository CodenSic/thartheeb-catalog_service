package com.thartheeb.catalog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Thartheeb Catalog Capability API", version = "v1",
    description = "Service-version Technician capability policy APIs.",
    contact = @Contact(name = "Thartheeb Engineering"),
    license = @License(name = "Proprietary - Internal Use")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer",
    bearerFormat = "JWT", description = "Admin JWT or internal-service token")
public class OpenApiConfig {
    @Bean GroupedOpenApi publicCatalogApi() {
        return GroupedOpenApi.builder().group("public").pathsToMatch("/v1/**").build();
    }

    @Bean GroupedOpenApi internalCatalogApi() {
        return GroupedOpenApi.builder().group("internal").pathsToMatch("/internal/**").build();
    }
}
