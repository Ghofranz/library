package tn.tp.bibliotheque.book_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tp.bibliotheque.book_service.entity.Book;
import tn.tp.bibliotheque.book_service.repository.BookRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // GET - Tous les livres
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // GET - Livre par ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Livres disponibles
    @GetMapping("/disponibles")
    public List<Book> getAvailableBooks() {
        return bookRepository.findByDisponible(true);
    }

    // GET - Recherche par auteur
    @GetMapping("/auteur/{auteur}")
    public List<Book> getBooksByAuteur(@PathVariable String auteur) {
        return bookRepository.findByAuteurContainingIgnoreCase(auteur);
    }

    // GET - Recherche par titre
    @GetMapping("/titre/{titre}")
    public List<Book> getBooksByTitre(@PathVariable String titre) {
        return bookRepository.findByTitreContainingIgnoreCase(titre);
    }

    // POST - Ajouter un livre
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        book.setDisponible(true);
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    // PUT - Modifier un livre
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitre(bookDetails.getTitre());
                    book.setAuteur(bookDetails.getAuteur());
                    book.setIsbn(bookDetails.getIsbn());
                    book.setDisponible(bookDetails.isDisponible());
                    return ResponseEntity.ok(bookRepository.save(book));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH - Changer disponibilité
    @PatchMapping("/{id}/disponibilite")
    public ResponseEntity<?> updateDisponibilite(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setDisponible(body.get("disponible"));
                    bookRepository.save(book);
                    return ResponseEntity.ok(book);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Supprimer un livre
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.ok(Map.of("message", "Livre supprimé avec succès"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}