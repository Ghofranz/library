package tn.tp.bibliotheque.notificationservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tn.tp.bibliotheque.notificationservice.model.Notification;
import tn.tp.bibliotheque.notificationservice.service.NotificationService;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET - Toutes les notifications
    @GetMapping
    public Flux<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // GET - Notification par ID
    @GetMapping("/{id}")
    public Mono<Notification> getNotificationById(@PathVariable String id) {
        return notificationService.getNotificationById(id);
    }

    // GET - Notifications par utilisateur
    @GetMapping("/user/{nomUtilisateur}")
    public Flux<Notification> getNotificationsByUser(@PathVariable String nomUtilisateur) {
        return notificationService.getNotificationsByUser(nomUtilisateur);
    }

    // GET - Notifications par type (EMPRUNT ou RETOUR)
    @GetMapping("/type/{type}")
    public Flux<Notification> getNotificationsByType(@PathVariable String type) {
        return notificationService.getNotificationsByType(type);
    }

    // GET - Notifications non lues
    @GetMapping("/unread")
    public Flux<Notification> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }

    // PUT - Marquer comme lu
    @PutMapping("/{id}/read")
    public Mono<Notification> markAsRead(@PathVariable String id) {
        return notificationService.markAsRead(id);
    }

    // GET - Compter les notifications
    @GetMapping("/count")
    public Mono<Map<String, Long>> countNotifications() {
        return notificationService.countNotifications()
                .map(count -> Map.of("total", count));
    }

    // GET - Stream de notifications en temps réel (Server-Sent Events)
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notification> streamNotifications() {
        return notificationService.getAllNotifications()
                .delayElements(Duration.ofMillis(500));
    }
}