package tn.tp.bibliotheque.search_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${book-service.url}")
    private String bookServiceUrl;

    @Value("${inventory-service.url}")
    private String inventoryServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public WebClient bookServiceClient() {
        return WebClient.builder()
                .baseUrl(bookServiceUrl)
                .build();
    }

    @Bean
    public WebClient inventoryServiceClient() {
        return WebClient.builder()
                .baseUrl(inventoryServiceUrl)
                .build();
    }
}