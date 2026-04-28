package tn.tp.bibliotheque.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tp.bibliotheque.user_service.entity.Role;
import tn.tp.bibliotheque.user_service.entity.User;
import tn.tp.bibliotheque.user_service.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET - Tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET - Utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Utilisateur par username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Utilisateur par email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Utilisateurs par rôle
    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable Role role) {
        return userService.getUsersByRole(role);
    }

    // GET - Recherche utilisateurs
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String q) {
        return userService.searchUsers(q);
    }

    // GET - Vérifier si utilisateur peut emprunter (endpoint public pour loan-service)
    @GetMapping("/public/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkUserCanBorrow(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.checkUserCanBorrow(userId));
    }

    // POST - Créer utilisateur
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = userService.createUser(user);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // PUT - Modifier utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = userService.updateUser(id, user);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // PATCH - Changer statut
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User.UserStatus status = User.UserStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }

    // PATCH - Changer rôle
    @PatchMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        User user = new User();
        user.setRole(role);
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // POST - Incrémenter compteur livres (appelé par loan-service)
    @PostMapping("/{id}/increment-books")
    public ResponseEntity<Void> incrementBooks(@PathVariable Long id) {
        userService.incrementBooksCount(id);
        return ResponseEntity.ok().build();
    }

    // POST - Décrémenter compteur livres (appelé par loan-service)
    @PostMapping("/{id}/decrement-books")
    public ResponseEntity<Void> decrementBooks(@PathVariable Long id) {
        userService.decrementBooksCount(id);
        return ResponseEntity.ok().build();
    }
}