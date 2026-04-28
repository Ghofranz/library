package tn.tp.bibliotheque.user_service.service;

import org.springframework.stereotype.Service;
import tn.tp.bibliotheque.user_service.entity.Role;
import tn.tp.bibliotheque.user_service.entity.User;
import tn.tp.bibliotheque.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
    }

    public Map<String, Object> createUser(User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "Username déjà utilisé");
            return response;
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("success", false);
            response.put("message", "Email déjà utilisé");
            return response;
        }

        User savedUser = userRepository.save(user);
        response.put("success", true);
        response.put("message", "Utilisateur créé avec succès");
        response.put("user", savedUser);

        return response;
    }

    public Map<String, Object> updateUser(Long id, User userDetails) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return response;
        }

        User user = optionalUser.get();

        if (userDetails.getFirstName() != null) user.setFirstName(userDetails.getFirstName());
        if (userDetails.getLastName() != null) user.setLastName(userDetails.getLastName());
        if (userDetails.getPhone() != null) user.setPhone(userDetails.getPhone());
        if (userDetails.getRole() != null) user.setRole(userDetails.getRole());

        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        response.put("success", true);
        response.put("message", "Utilisateur mis à jour");
        response.put("user", savedUser);

        return response;
    }

    public Map<String, Object> updateUserStatus(Long id, User.UserStatus status) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return response;
        }

        User user = optionalUser.get();
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        response.put("success", true);
        response.put("message", "Statut mis à jour: " + status);
        response.put("user", user);

        return response;
    }

    public Map<String, Object> checkUserCanBorrow(Long userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            response.put("canBorrow", false);
            response.put("reason", "Utilisateur non trouvé");
            return response;
        }

        User user = optionalUser.get();

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            response.put("canBorrow", false);
            response.put("reason", "Compte " + user.getStatus().toString().toLowerCase());
            return response;
        }

        if (user.getCurrentBooksCount() >= user.getMaxBooksAllowed()) {
            response.put("canBorrow", false);
            response.put("reason", "Limite d'emprunts atteinte (" + user.getMaxBooksAllowed() + " livres max)");
            return response;
        }

        response.put("canBorrow", true);
        response.put("currentBooks", user.getCurrentBooksCount());
        response.put("maxBooks", user.getMaxBooksAllowed());
        response.put("remainingSlots", user.getMaxBooksAllowed() - user.getCurrentBooksCount());

        return response;
    }

    public void incrementBooksCount(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setCurrentBooksCount(user.getCurrentBooksCount() + 1);
            userRepository.save(user);
        });
    }

    public void decrementBooksCount(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getCurrentBooksCount() > 0) {
                user.setCurrentBooksCount(user.getCurrentBooksCount() - 1);
                userRepository.save(user);
            }
        });
    }
}