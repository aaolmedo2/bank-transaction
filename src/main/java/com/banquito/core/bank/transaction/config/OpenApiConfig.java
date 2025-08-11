package com.banquito.core.bank.transaction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Transaction Microservice API")
                        .description("Microservicio para el procesamiento de transacciones bancarias. " +
                                "Este servicio maneja depósitos, retiros y consultas de estado de transacciones " +
                                "a través de mensajería asíncrona con ActiveMQ.")
                        .version(appVersion)
                        .contact(new Contact()
                                .name("Banquito Core Team")
                                .email("core-team@banquito.com")
                                .url("https://banquito.com"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.banquito.com")
                                .description("Servidor de producción")))
                .tags(List.of(
                        new Tag()
                                .name("Transacciones")
                                .description("Operaciones relacionadas con transacciones bancarias"),
                        new Tag()
                                .name("Sistema")
                                .description("Endpoints de monitoreo y estado del sistema")));
    }
}
