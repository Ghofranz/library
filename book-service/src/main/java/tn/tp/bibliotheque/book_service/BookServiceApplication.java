package tn.tp.bibliotheque.book_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tn.tp.bibliotheque.book_service.entity.Book;
import tn.tp.bibliotheque.book_service.repository.BookRepository;

import java.util.List;

@SpringBootApplication
public class BookServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(BookRepository repository) {
		return args -> {
			repository.saveAll(List.of(
					new Book("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2-07-040850-4", true),
					new Book("Les Misérables", "Victor Hugo", "978-2-07-040851-1", true),
					new Book("L'Étranger", "Albert Camus", "978-2-07-040852-8", false),
					new Book("Germinal", "Émile Zola", "978-2-07-040853-5", true),
					new Book("Madame Bovary", "Gustave Flaubert", "978-2-07-040854-2", true)
			));
			System.out.println("=== Base de données initialisée avec 5 livres ===");
		};
	}
}