package tn.tp.bibliotheque.notificationservice.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tn.tp.bibliotheque.notificationservice.model.Notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationRepository {

    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();

    public Mono<Notification> save(Notification notification) {
        notifications.put(notification.getId(), notification);
        return Mono.just(notification);
    }

    public Flux<Notification> findAll() {
        return Flux.fromIterable(notifications.values());
    }

    public Mono<Notification> findById(String id) {
        return Mono.justOrEmpty(notifications.get(id));
    }

    public Flux<Notification> findByNomUtilisateur(String nomUtilisateur) {
        return Flux.fromIterable(notifications.values())
                .filter(n -> n.getNomUtilisateur().equals(nomUtilisateur));
    }

    public Flux<Notification> findByTypeNotification(String type) {
        return Flux.fromIterable(notifications.values())
                .filter(n -> n.getTypeNotification().equals(type));
    }

    public Flux<Notification> findUnread() {
        return Flux.fromIterable(notifications.values())
                .filter(n -> !n.isLu());
    }

    public Mono<Notification> markAsRead(String id) {
        Notification notification = notifications.get(id);
        if (notification != null) {
            notification.setLu(true);
            return Mono.just(notification);
        }
        return Mono.empty();
    }

    public Mono<Long> count() {
        return Mono.just((long) notifications.size());
    }

    public Mono<Void> deleteAll() {
        notifications.clear();
        return Mono.empty();
    }
}