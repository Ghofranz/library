package tn.tp.bibliotheque.loan_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.tp.bibliotheque.loan_service.config.RabbitConfig;
import tn.tp.bibliotheque.loan_service.dto.BookDto;
import tn.tp.bibliotheque.loan_service.dto.LoanNotification;
import tn.tp.bibliotheque.loan_service.entity.Loan;
import tn.tp.bibliotheque.loan_service.repository.LoanRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${book-service.url}")
    private String bookServiceUrl;

    public LoanService(LoanRepository loanRepository, RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.loanRepository = loanRepository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getLoansByUser(String nomUtilisateur) {
        return loanRepository.findByNomUtilisateur(nomUtilisateur);
    }

    public List<Loan> getLoansByStatus(Loan.LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public Map<String, Object> createLoan(Long bookId, String nomUtilisateur, String token) {
        Map<String, Object> response = new HashMap<>();

        // Vérifier si le livre existe et est disponible
        BookDto book = getBookFromBookService(bookId, token);

        if (book == null) {
            response.put("success", false);
            response.put("message", "Livre non trouvé");
            return response;
        }

        if (!book.isDisponible()) {
            response.put("success", false);
            response.put("message", "Le livre n'est pas disponible");
            return response;
        }

        // Créer l'emprunt
        Loan loan = new Loan(
                bookId,
                nomUtilisateur,
                LocalDateTime.now(),
                LocalDateTime.now().plusWeeks(2) // 2 semaines de prêt
        );

        Loan savedLoan = loanRepository.save(loan);

        // Mettre à jour la disponibilité du livre
        updateBookAvailability(bookId, false, token);

        // Envoyer notification via RabbitMQ
        LoanNotification notification = new LoanNotification(
                savedLoan.getId(),
                bookId,
                book.getTitre(),
                nomUtilisateur,
                savedLoan.getDateEmprunt(),
                savedLoan.getDateRetourPrevue(),
                "EMPRUNT"
        );

        sendNotification(notification);

        response.put("success", true);
        response.put("message", "Emprunt créé avec succès");
        response.put("loan", savedLoan);
        response.put("book", book);

        return response;
    }

    public Map<String, Object> returnBook(Long loanId, String token) {
        Map<String, Object> response = new HashMap<>();

        Optional<Loan> optionalLoan = loanRepository.findById(loanId);

        if (optionalLoan.isEmpty()) {
            response.put("success", false);
            response.put("message", "Emprunt non trouvé");
            return response;
        }

        Loan loan = optionalLoan.get();

        if (loan.getStatus() == Loan.LoanStatus.RETOURNE) {
            response.put("success", false);
            response.put("message", "Ce livre a déjà été retourné");
            return response;
        }

        // Mettre à jour l'emprunt
        loan.setDateRetourEffective(LocalDateTime.now());

        if (LocalDateTime.now().isAfter(loan.getDateRetourPrevue())) {
            loan.setStatus(Loan.LoanStatus.EN_RETARD);
        } else {
            loan.setStatus(Loan.LoanStatus.RETOURNE);
        }

        loanRepository.save(loan);

        // Rendre le livre disponible
        updateBookAvailability(loan.getBookId(), true, token);

        // Récupérer les infos du livre pour la notification
        BookDto book = getBookFromBookService(loan.getBookId(), token);

        // Envoyer notification via RabbitMQ
        LoanNotification notification = new LoanNotification(
                loan.getId(),
                loan.getBookId(),
                book != null ? book.getTitre() : "Livre inconnu",
                loan.getNomUtilisateur(),
                loan.getDateEmprunt(),
                loan.getDateRetourPrevue(),
                "RETOUR"
        );

        sendNotification(notification);

        response.put("success", true);
        response.put("message", "Livre retourné avec succès");
        response.put("loan", loan);

        return response;
    }

    private BookDto getBookFromBookService(Long bookId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<BookDto> response = restTemplate.exchange(
                    bookServiceUrl + "/books/" + bookId,
                    HttpMethod.GET,
                    entity,
                    BookDto.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du livre: " + e.getMessage());
            return null;
        }
    }

    private void updateBookAvailability(Long bookId, boolean disponible, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Boolean> body = new HashMap<>();
            body.put("disponible", disponible);

            HttpEntity<Map<String, Boolean>> entity = new HttpEntity<>(body, headers);

            restTemplate.exchange(
                    bookServiceUrl + "/books/" + bookId + "/disponibilite",
                    HttpMethod.PATCH,
                    entity,
                    Object.class
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du livre: " + e.getMessage());
        }
    }

    private void sendNotification(LoanNotification notification) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.ROUTING_KEY,
                    notification
            );
            System.out.println("Notification envoyée: " + notification);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
}