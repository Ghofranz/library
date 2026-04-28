package tn.tp.bibliotheque.notificationservice.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tn.tp.bibliotheque.notificationservice.dto.LoanNotification;
import tn.tp.bibliotheque.notificationservice.model.Notification;
import tn.tp.bibliotheque.notificationservice.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Mono<Notification> createNotification(LoanNotification loanNotification) {
        String message = generateMessage(loanNotification);

        Notification notification = new Notification(
                loanNotification.getLoanId(),
                loanNotification.getBookId(),
                loanNotification.getBookTitre(),
                loanNotification.getNomUtilisateur(),
                loanNotification.getTypeNotification(),
                message
        );

        return repository.save(notification);
    }

    private String generateMessage(LoanNotification loan) {
        if ("EMPRUNT".equals(loan.getTypeNotification())) {
            return String.format(
                    "📚 Nouvel emprunt : %s a emprunté le livre '%s'. Date de retour prévue : %s",
                    loan.getNomUtilisateur(),
                    loan.getBookTitre(),
                    loan.getDateRetourPrevue().toLocalDate()
            );
        } else if ("RETOUR".equals(loan.getTypeNotification())) {
            return String.format(
                    "✅ Retour confirmé : %s a retourné le livre '%s'.",
                    loan.getNomUtilisateur(),
                    loan.getBookTitre()
            );
        }
        return "Notification de la bibliothèque";
    }

    public Flux<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public Mono<Notification> getNotificationById(String id) {
        return repository.findById(id);
    }

    public Flux<Notification> getNotificationsByUser(String nomUtilisateur) {
        return repository.findByNomUtilisateur(nomUtilisateur);
    }

    public Flux<Notification> getNotificationsByType(String type) {
        return repository.findByTypeNotification(type);
    }

    public Flux<Notification> getUnreadNotifications() {
        return repository.findUnread();
    }

    public Mono<Notification> markAsRead(String id) {
        return repository.markAsRead(id);
    }

    public Mono<Long> countNotifications() {
        return repository.count();
    }
}