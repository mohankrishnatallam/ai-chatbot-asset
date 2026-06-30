package com.cx.asset.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aiChatbotOpenApi(@Value("${server.port:8082}") int serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Chatbot API")
                        .description("""
                                REST API for the AI Chatbot application.

                                **Headers**
                                - `X-Session-Id` — required on assistant endpoints that operate on a chat session
                                - `X-User-Id` — required on assistant and saved-prompt endpoints (value from auth login/register)

                                **Swagger UI:** `/swagger-ui.html`
                                **OpenAPI JSON:** `/v3/api-docs`
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("AI Chatbot Asset")))
                .servers(List.of(new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local development")));
    }
}
