package tn.tp.bibliotheque.loan_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tp.bibliotheque.loan_service.entity.Loan;
import tn.tp.bibliotheque.loan_service.service.LoanService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // GET - Tous les emprunts
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    // GET - Emprunt par ID
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Emprunts par utilisateur
    @GetMapping("/user/{nomUtilisateur}")
    public List<Loan> getLoansByUser(@PathVariable String nomUtilisateur) {
        return loanService.getLoansByUser(nomUtilisateur);
    }

    // GET - Emprunts en cours
    @GetMapping("/en-cours")
    public List<Loan> getActiveLoans() {
        return loanService.getLoansByStatus(Loan.LoanStatus.EN_COURS);
    }

    // POST - Créer un emprunt
    @PostMapping
    public ResponseEntity<Map<String, Object>> createLoan(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {

        System.out.println("=== REQUÊTE CRÉATION EMPRUNT REÇUE ===");
        System.out.println("Auth Header: " + authHeader);
        System.out.println("Request Body: " + request);

        Long bookId = Long.valueOf(request.get("bookId").toString());
        String nomUtilisateur = request.get("nomUtilisateur").toString();

        // Extraire le token (enlever "Bearer " s'il est présent)
        String token = authHeader;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        System.out.println("Token extrait (début): " + token.substring(0, Math.min(20, token.length())) + "...");

        Map<String, Object> response = loanService.createLoan(bookId, nomUtilisateur, token);

        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // PUT - Retourner un livre
    @PutMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> returnBook(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        Map<String, Object> response = loanService.returnBook(id, token);

        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}