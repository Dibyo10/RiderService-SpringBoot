package com.example.RiderService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Rider Service API",
                version = "v1",
                description = "APIs for rider onboarding, availability, and assignments"
        )
)
public class OpenAPIConfig {
}
