package tn.tp.bibliotheque.inventory_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long bookId;

    private String bookTitle; // Cache du titre pour affichage rapide

    private int totalCopies;

    private int availableCopies;

    private int reservedCopies;

    private int damagedCopies;

    private String location; // Ex: "Rayon A - Étagère 3"

    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    public enum StockStatus {
        IN_STOCK,       // Copies disponibles
        LOW_STOCK,      // Moins de 3 copies
        OUT_OF_STOCK,   // Aucune copie disponible
        DISCONTINUED    // Livre retiré du catalogue
    }

    public Inventory() {
        this.lastUpdated = LocalDateTime.now();
        this.reservedCopies = 0;
        this.damagedCopies = 0;
    }

    public Inventory(Long bookId, String bookTitle, int totalCopies, String location) {
        this();
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.location = location;
        updateStockStatus();
    }

    public void updateStockStatus() {
        if (availableCopies == 0) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (availableCopies <= 2) {
            this.stockStatus = StockStatus.LOW_STOCK;
        } else {
            this.stockStatus = StockStatus.IN_STOCK;
        }
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
        updateStockStatus();
    }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        updateStockStatus();
    }

    public int getReservedCopies() { return reservedCopies; }
    public void setReservedCopies(int reservedCopies) { this.reservedCopies = reservedCopies; }

    public int getDamagedCopies() { return damagedCopies; }
    public void setDamagedCopies(int damagedCopies) { this.damagedCopies = damagedCopies; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public StockStatus getStockStatus() { return stockStatus; }
    public void setStockStatus(StockStatus stockStatus) { this.stockStatus = stockStatus; }

    public int getBorrowedCopies() {
        return totalCopies - availableCopies - damagedCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }
}