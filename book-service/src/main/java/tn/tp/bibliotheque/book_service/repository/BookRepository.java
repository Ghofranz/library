package tn.tp.bibliotheque.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.tp.bibliotheque.book_service.entity.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByDisponible(boolean disponible);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuteurContainingIgnoreCase(String auteur);

    List<Book> findByTitreContainingIgnoreCase(String titre);
}