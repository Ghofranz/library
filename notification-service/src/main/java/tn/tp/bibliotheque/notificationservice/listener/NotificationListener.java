package tn.tp.bibliotheque.notificationservice.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tn.tp.bibliotheque.notificationservice.config.RabbitConfig;
import tn.tp.bibliotheque.notificationservice.dto.LoanNotification;
import tn.tp.bibliotheque.notificationservice.service.NotificationService;

@Component
public class NotificationListener {

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receiveNotification(LoanNotification loanNotification) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("📬 MESSAGE REÇU DE RABBITMQ");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Type: " + loanNotification.getTypeNotification());
        System.out.println("Livre: " + loanNotification.getBookTitre());
        System.out.println("Utilisateur: " + loanNotification.getNomUtilisateur());
        System.out.println("Loan ID: " + loanNotification.getLoanId());
        System.out.println("═══════════════════════════════════════════════════════");

        // Créer et sauvegarder la notification
        notificationService.createNotification(loanNotification)
                .subscribe(notification -> {
                    System.out.println("✅ Notification créée avec ID: " + notification.getId());
                    System.out.println("📝 Message: " + notification.getMessage());
                    System.out.println("═══════════════════════════════════════════════════════\n");
                });
    }
}