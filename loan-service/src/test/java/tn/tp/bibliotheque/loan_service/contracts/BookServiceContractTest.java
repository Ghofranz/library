package tn.tp.bibliotheque.loan_service.contracts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import tn.tp.bibliotheque.loan_service.LoanServiceApplication;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = LoanServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@AutoConfigureStubRunner(
        ids = "tn.tp.bibliotheque:book-service:+:stubs:8082",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class BookServiceContractTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    void shouldGetBookById() {
        // Préparer la requête avec header Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Appeler le stub
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8082/books/1",
                HttpMethod.GET,
                entity,
                String.class
        );

        // Vérifier la réponse
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Le Petit Prince"));
        assertTrue(response.getBody().contains("Antoine de Saint-Exupéry"));

        System.out.println("✅ Test shouldGetBookById réussi !");
        System.out.println("Réponse: " + response.getBody());
    }

    @Test
    void shouldGetAvailableBooks() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8082/books/disponibles",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Le Petit Prince"));

        System.out.println("✅ Test shouldGetAvailableBooks réussi !");
        System.out.println("Réponse: " + response.getBody());
    }
}
