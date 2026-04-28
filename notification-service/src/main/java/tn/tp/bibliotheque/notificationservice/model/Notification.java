package tn.tp.bibliotheque.notificationservice.model;

import java.time.LocalDateTime;

public class Notification {

    private String id;
    private Long loanId;
    private Long bookId;
    private String bookTitre;
    private String nomUtilisateur;
    private String typeNotification;
    private String message;
    private LocalDateTime dateCreation;
    private boolean lu;

    public Notification() {
        this.dateCreation = LocalDateTime.now();
        this.lu = false;
    }

    public Notification(Long loanId, Long bookId, String bookTitre, String nomUtilisateur,
                        String typeNotification, String message) {
        this.id = java.util.UUID.randomUUID().toString();
        this.loanId = loanId;
        this.bookId = bookId;
        this.bookTitre = bookTitre;
        this.nomUtilisateur = nomUtilisateur;
        this.typeNotification = typeNotification;
        this.message = message;
        this.dateCreation = LocalDateTime.now();
        this.lu = false;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitre() {
        return bookTitre;
    }

    public void setBookTitre(String bookTitre) {
        this.bookTitre = bookTitre;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(String typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }
}