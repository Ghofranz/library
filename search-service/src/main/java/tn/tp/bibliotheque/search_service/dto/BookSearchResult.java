package tn.tp.bibliotheque.search_service.dto;

public class BookSearchResult {

    private Long bookId;
    private String titre;
    private String auteur;
    private String isbn;
    private boolean disponible;
    private int totalCopies;
    private int availableCopies;
    private String location;
    private String stockStatus;
    private double relevanceScore;

    public BookSearchResult() {
    }

    public BookSearchResult(BookDto book, InventoryDto inventory) {
        this.bookId = book.getId();
        this.titre = book.getTitre();
        this.auteur = book.getAuteur();
        this.isbn = book.getIsbn();
        this.disponible = book.isDisponible();

        if (inventory != null) {
            this.totalCopies = inventory.getTotalCopies();
            this.availableCopies = inventory.getAvailableCopies();
            this.location = inventory.getLocation();
            this.stockStatus = inventory.getStockStatus();
        }

        this.relevanceScore = 0.0;
    }

    // Getters et Setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
}