package tn.tp.bibliotheque.user_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tn.tp.bibliotheque.user_service.entity.Role;
import tn.tp.bibliotheque.user_service.entity.User;
import tn.tp.bibliotheque.user_service.repository.UserRepository;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository repository) {
		return args -> {
			// Admin
			User admin = new User("admin", "admin@library.com", "Admin", "System");
			admin.setRole(Role.ADMIN);
			repository.save(admin);

			// Librarian
			User librarian = new User("librarian", "librarian@library.com", "Marie", "Dupont");
			librarian.setRole(Role.LIBRARIAN);
			repository.save(librarian);

			// Members
			User member1 = new User("jean", "jean@example.com", "Jean", "Martin");
			member1.setRole(Role.MEMBER);
			repository.save(member1);

			User member2 = new User("pierre", "pierre@example.com", "Pierre", "Bernard");
			member2.setRole(Role.PREMIUM_MEMBER);
			repository.save(member2);

			User member3 = new User("sophie", "sophie@example.com", "Sophie", "Leroy");
			member3.setRole(Role.MEMBER);
			member3.setStatus(User.UserStatus.BLOCKED);
			repository.save(member3);

			System.out.println("=== Base de données initialisée avec 5 utilisateurs ===");
		};
	}
}