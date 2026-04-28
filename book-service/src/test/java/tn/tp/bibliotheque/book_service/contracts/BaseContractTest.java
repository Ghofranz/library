package tn.tp.bibliotheque.book_service.contracts;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;
import tn.tp.bibliotheque.book_service.BookServiceApplication;
import tn.tp.bibliotheque.book_service.entity.Book;
import tn.tp.bibliotheque.book_service.filter.JwtAuthFilter;
import tn.tp.bibliotheque.book_service.repository.BookRepository;
import tn.tp.bibliotheque.book_service.service.JwtService;

import java.util.List;
import java.util.Optional;

@SpringBootTest(
        classes = BookServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ActiveProfiles("test")
public class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    public void setup() throws Exception {
        RestAssuredMockMvc.webAppContextSetup(context);

        // Mock du JwtService pour accepter tous les tokens
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("admin");
        Mockito.when(jwtService.isTokenExpired(Mockito.anyString())).thenReturn(false);

        // Mock du JwtAuthFilter pour laisser passer toutes les requêtes
        Mockito.doAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());

        // Mock des données de livre
        Book book1 = new Book("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2-07-040850-4", true);
        book1.setId(1L);

        Book book2 = new Book("Les Misérables", "Victor Hugo", "978-2-07-040851-1", true);
        book2.setId(2L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        Mockito.when(bookRepository.findByDisponible(true)).thenReturn(List.of(book1, book2));
        Mockito.when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
    }
}