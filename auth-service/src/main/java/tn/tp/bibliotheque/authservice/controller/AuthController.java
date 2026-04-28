package tn.tp.bibliotheque.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tp.bibliotheque.authservice.model.User;
import tn.tp.bibliotheque.authservice.service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        // Utilisateurs en dur pour le test
        if (("admin".equals(user.getUsername()) && "admin123".equals(user.getPassword())) ||
                ("user".equals(user.getUsername()) && "user123".equals(user.getPassword()))) {

            String token = jwtService.generateToken(user.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("message", "Connexion réussie");

            return ResponseEntity.ok(response);
        }

        Map<String, String> error = new HashMap<>();
        error.put("message", "Identifiants invalides");
        return ResponseEntity.status(401).body(error);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.extractUsername(token);

                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);

                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(400).body(Map.of("message", "Token manquant"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Token invalide"));
        }
    }
}