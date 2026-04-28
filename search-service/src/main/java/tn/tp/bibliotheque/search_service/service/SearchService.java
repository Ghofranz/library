package tn.tp.bibliotheque.search_service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tn.tp.bibliotheque.search_service.dto.BookDto;
import tn.tp.bibliotheque.search_service.dto.BookSearchResult;
import tn.tp.bibliotheque.search_service.dto.InventoryDto;

import java.time.Duration;
import java.util.Comparator;
import java.util.Map;

@Service
public class SearchService {

    private final WebClient bookServiceClient;
    private final WebClient inventoryServiceClient;

    public SearchService(
            @Qualifier("bookServiceClient") WebClient bookServiceClient,
            @Qualifier("inventoryServiceClient") WebClient inventoryServiceClient) {
        this.bookServiceClient = bookServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    /**
     * Recherche tous les livres avec leurs informations d'inventaire
     * Utilise Flux pour le streaming réactif
     */
    public Flux<BookSearchResult> getAllBooksWithInventory(String token) {
        System.out.println("🔍 Recherche de tous les livres...");

        return getBooksFromBookService(token)
                .flatMap(book -> enrichWithInventory(book, token))
                .doOnNext(result -> System.out.println("📚 Trouvé: " + result.getTitre()))
                .doOnComplete(() -> System.out.println("✅ Recherche terminée"));
    }

    /**
     * Recherche par titre (insensible à la casse)
     */
    public Flux<BookSearchResult> searchByTitle(String title, String token) {
        System.out.println("🔍 Recherche par titre: " + title);

        return getBooksFromBookService(token)
                .filter(book -> book.getTitre().toLowerCase().contains(title.toLowerCase()))
                .flatMap(book -> enrichWithInventory(book, token))
                .map(result -> {
                    // Calculer le score de pertinence
                    double score = calculateRelevanceScore(result.getTitre(), title);
                    result.setRelevanceScore(score);
                    return result;
                })
                .sort(Comparator.comparingDouble(BookSearchResult::getRelevanceScore).reversed());
    }

    /**
     * Recherche par auteur
     */
    public Flux<BookSearchResult> searchByAuthor(String author, String token) {
        System.out.println("🔍 Recherche par auteur: " + author);

        return getBooksFromBookService(token)
                .filter(book -> book.getAuteur().toLowerCase().contains(author.toLowerCase()))
                .flatMap(book -> enrichWithInventory(book, token))
                .map(result -> {
                    double score = calculateRelevanceScore(result.getAuteur(), author);
                    result.setRelevanceScore(score);
                    return result;
                })
                .sort(Comparator.comparingDouble(BookSearchResult::getRelevanceScore).reversed());
    }

    /**
     * Recherche globale (titre OU auteur)
     */
    public Flux<BookSearchResult> globalSearch(String query, String token) {
        System.out.println("🔍 Recherche globale: " + query);

        return getBooksFromBookService(token)
                .filter(book ->
                        book.getTitre().toLowerCase().contains(query.toLowerCase()) ||
                                book.getAuteur().toLowerCase().contains(query.toLowerCase()))
                .flatMap(book -> enrichWithInventory(book, token))
                .map(result -> {
                    double titleScore = calculateRelevanceScore(result.getTitre(), query);
                    double authorScore = calculateRelevanceScore(result.getAuteur(), query);
                    result.setRelevanceScore(Math.max(titleScore, authorScore));
                    return result;
                })
                .sort(Comparator.comparingDouble(BookSearchResult::getRelevanceScore).reversed());
    }

    /**
     * Recherche uniquement les livres disponibles
     */
    public Flux<BookSearchResult> searchAvailableBooks(String token) {
        System.out.println("🔍 Recherche des livres disponibles...");

        return getBooksFromBookService(token)
                .filter(BookDto::isDisponible)
                .flatMap(book -> enrichWithInventory(book, token))
                .filter(result -> result.getAvailableCopies() > 0);
    }

    /**
     * Recherche par ISBN
     */
    public Mono<BookSearchResult> searchByIsbn(String isbn, String token) {
        System.out.println("🔍 Recherche par ISBN: " + isbn);

        return getBooksFromBookService(token)
                .filter(book -> book.getIsbn().equals(isbn))
                .next()
                .flatMap(book -> enrichWithInventory(book, token));
    }

    /**
     * Obtenir un livre par ID avec toutes les infos
     */
    public Mono<BookSearchResult> getBookById(Long bookId, String token) {
        System.out.println("🔍 Recherche livre ID: " + bookId);

        return bookServiceClient.get()
                .uri("/books/" + bookId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(BookDto.class)
                .flatMap(book -> enrichWithInventory(book, token))
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> System.err.println("❌ Erreur: " + e.getMessage()));
    }

    /**
     * Recherche avancée avec filtres multiples
     */
    public Flux<BookSearchResult> advancedSearch(
            String title,
            String author,
            Boolean availableOnly,
            String token) {

        System.out.println("🔍 Recherche avancée - Titre: " + title + ", Auteur: " + author + ", Dispo: " + availableOnly);

        return getBooksFromBookService(token)
                .filter(book -> {
                    boolean matches = true;

                    if (title != null && !title.isEmpty()) {
                        matches = book.getTitre().toLowerCase().contains(title.toLowerCase());
                    }

                    if (matches && author != null && !author.isEmpty()) {
                        matches = book.getAuteur().toLowerCase().contains(author.toLowerCase());
                    }

                    if (matches && availableOnly != null && availableOnly) {
                        matches = book.isDisponible();
                    }

                    return matches;
                })
                .flatMap(book -> enrichWithInventory(book, token))
                .filter(result -> {
                    if (availableOnly != null && availableOnly) {
                        return result.getAvailableCopies() > 0;
                    }
                    return true;
                });
    }

    /**
     * Statistiques de la bibliothèque (agrégation réactive)
     */
    public Mono<Map<String, Object>> getLibraryStats(String token) {
        System.out.println("📊 Calcul des statistiques...");

        Mono<Long> totalBooks = getBooksFromBookService(token).count();
        Mono<Long> availableBooks = getBooksFromBookService(token)
                .filter(BookDto::isDisponible)
                .count();

        return Mono.zip(totalBooks, availableBooks)
                .map(tuple -> Map.of(
                        "totalBooks", tuple.getT1(),
                        "availableBooks", tuple.getT2(),
                        "borrowedBooks", tuple.getT1() - tuple.getT2()
                ));
    }

    // === Méthodes privées ===

    private Flux<BookDto> getBooksFromBookService(String token) {
        return bookServiceClient.get()
                .uri("/books")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(BookDto.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(e -> {
                    System.err.println("❌ Erreur book-service: " + e.getMessage());
                    return Flux.empty();
                });
    }

    private Mono<BookSearchResult> enrichWithInventory(BookDto book, String token) {
        return inventoryServiceClient.get()
                .uri("/inventory/public/check/" + book.getId())
                .retrieve()
                .bodyToMono(InventoryDto.class)
                .map(inventory -> new BookSearchResult(book, inventory))
                .onErrorResume(e -> {
                    System.err.println("⚠️ Pas d'inventaire pour livre " + book.getId());
                    return Mono.just(new BookSearchResult(book, null));
                })
                .timeout(Duration.ofSeconds(5));
    }

    private double calculateRelevanceScore(String text, String query) {
        if (text == null || query == null) return 0.0;

        String lowerText = text.toLowerCase();
        String lowerQuery = query.toLowerCase();

        // Match exact = score max
        if (lowerText.equals(lowerQuery)) return 1.0;

        // Commence par la requête = score élevé
        if (lowerText.startsWith(lowerQuery)) return 0.9;

        // Contient la requête = score moyen
        if (lowerText.contains(lowerQuery)) return 0.7;

        // Mots correspondants
        String[] queryWords = lowerQuery.split("\\s+");
        int matchCount = 0;
        for (String word : queryWords) {
            if (lowerText.contains(word)) matchCount++;
        }

        return (double) matchCount / queryWords.length * 0.5;
    }
}