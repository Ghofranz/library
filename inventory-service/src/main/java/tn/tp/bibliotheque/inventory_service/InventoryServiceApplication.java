package tn.tp.bibliotheque.inventory_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tn.tp.bibliotheque.inventory_service.entity.Inventory;
import tn.tp.bibliotheque.inventory_service.repository.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(InventoryRepository repository) {
		return args -> {
			// Correspondance avec les livres de book-service
			repository.save(new Inventory(1L, "Le Petit Prince", 5, "Rayon A - Étagère 1"));
			repository.save(new Inventory(2L, "Les Misérables", 3, "Rayon A - Étagère 2"));
			repository.save(new Inventory(3L, "L'Étranger", 2, "Rayon B - Étagère 1"));
			repository.save(new Inventory(4L, "Germinal", 4, "Rayon B - Étagère 2"));
			repository.save(new Inventory(5L, "Madame Bovary", 1, "Rayon C - Étagère 1"));

			System.out.println("=== Inventaire initialisé avec 5 livres ===");
		};
	}
}