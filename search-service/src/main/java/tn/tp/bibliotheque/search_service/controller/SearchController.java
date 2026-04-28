package tn.tp.bibliotheque.search_service.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tn.tp.bibliotheque.search_service.dto.BookSearchResult;
import tn.tp.bibliotheque.search_service.service.SearchService;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /search - Tous les livres avec inventaire
     */
    @GetMapping
    public Flux<BookSearchResult> getAllBooks(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.getAllBooksWithInventory(token);
    }

    /**
     * GET /search/stream - Stream en temps réel (Server-Sent Events)
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookSearchResult> streamAllBooks(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.getAllBooksWithInventory(token)
                .delayElements(Duration.ofMillis(500)); // Délai pour effet visuel
    }

    /**
     * GET /search/title/{title} - Recherche par titre
     */
    @GetMapping("/title/{title}")
    public Flux<BookSearchResult> searchByTitle(
            @PathVariable String title,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.searchByTitle(title, token);
    }

    /**
     * GET /search/author/{author} - Recherche par auteur
     */
    @GetMapping("/author/{author}")
    public Flux<BookSearchResult> searchByAuthor(
            @PathVariable String author,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.searchByAuthor(author, token);
    }

    /**
     * GET /search/query?q=xxx - Recherche globale
     */
    @GetMapping("/query")
    public Flux<BookSearchResult> globalSearch(
            @RequestParam String q,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.globalSearch(q, token);
    }

    /**
     * GET /search/available - Livres disponibles uniquement
     */
    @GetMapping("/available")
    public Flux<BookSearchResult> searchAvailable(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.searchAvailableBooks(token);
    }

    /**
     * GET /search/isbn/{isbn} - Recherche par ISBN
     */
    @GetMapping("/isbn/{isbn}")
    public Mono<BookSearchResult> searchByIsbn(
            @PathVariable String isbn,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.searchByIsbn(isbn, token);
    }

    /**
     * GET /search/book/{id} - Détails d'un livre
     */
    @GetMapping("/book/{id}")
    public Mono<BookSearchResult> getBookById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.getBookById(id, token);
    }

    /**
     * GET /search/advanced - Recherche avancée avec filtres
     */
    @GetMapping("/advanced")
    public Flux<BookSearchResult> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean available,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.advancedSearch(title, author, available, token);
    }

    /**
     * GET /search/stats - Statistiques de la bibliothèque
     */
    @GetMapping("/stats")
    public Mono<Map<String, Object>> getStats(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.getLibraryStats(token);
    }

    /**
     * GET /search/stream/query - Stream de recherche en temps réel
     */
    @GetMapping(value = "/stream/query", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookSearchResult> streamSearch(
            @RequestParam String q,
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        return searchService.globalSearch(q, token)
                .delayElements(Duration.ofMillis(300));
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}