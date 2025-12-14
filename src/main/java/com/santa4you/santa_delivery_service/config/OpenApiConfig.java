package com.santa4you.santa_delivery_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI santa4YouOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Santa4You API")
                        .description("Christmas Delivery Service APIs - Register users, manage wishlists, and track Santa.")
                        .version("v1.0.0"));
    }
}