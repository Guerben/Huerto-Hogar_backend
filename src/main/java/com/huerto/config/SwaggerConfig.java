package com.huerto.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Huerto Hogar API")
                        .version("1.0.0")
                        .description("API REST para la gestión de productos orgánicos y herramientas de huerto urbano. " +
                                "Esta API proporciona endpoints para la autenticación con JWT, gestión de productos, " +
                                "órdenes de compra, ventas y usuarios con roles diferenciados.")
                        .contact(new Contact()
                                .name("Equipo Huerto Hogar")
                                .email("huertohogar.info@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Producción")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Autenticación JWT. Ingrese el token obtenido del endpoint /api/auth/login o /api/auth/register")));
    }
}

