package tn.tp.bibliotheque.inventory_service.service;

import org.springframework.stereotype.Service;
import tn.tp.bibliotheque.inventory_service.entity.Inventory;
import tn.tp.bibliotheque.inventory_service.repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Optional<Inventory> getInventoryByBookId(Long bookId) {
        return inventoryRepository.findByBookId(bookId);
    }

    public List<Inventory> getAvailableInventory() {
        return inventoryRepository.findAllAvailable();
    }

    public List<Inventory> getLowStockInventory() {
        return inventoryRepository.findLowStock();
    }

    public List<Inventory> getOutOfStockInventory() {
        return inventoryRepository.findByStockStatus(Inventory.StockStatus.OUT_OF_STOCK);
    }

    public Map<String, Object> createInventory(Inventory inventory) {
        Map<String, Object> response = new HashMap<>();

        if (inventoryRepository.existsByBookId(inventory.getBookId())) {
            response.put("success", false);
            response.put("message", "Inventaire déjà existant pour ce livre");
            return response;
        }

        inventory.updateStockStatus();
        Inventory saved = inventoryRepository.save(inventory);

        response.put("success", true);
        response.put("message", "Inventaire créé");
        response.put("inventory", saved);

        System.out.println("📦 Inventaire créé pour livre " + inventory.getBookId() +
                " - " + inventory.getTotalCopies() + " copies");

        return response;
    }

    public Map<String, Object> borrowBook(Long bookId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookId);

        if (optionalInventory.isEmpty()) {
            response.put("success", false);
            response.put("message", "Livre non trouvé dans l'inventaire");
            return response;
        }

        Inventory inventory = optionalInventory.get();

        if (inventory.getAvailableCopies() <= 0) {
            response.put("success", false);
            response.put("message", "Aucune copie disponible");
            response.put("availableCopies", 0);
            return response;
        }

        inventory.setAvailableCopies(inventory.getAvailableCopies() - 1);
        inventory.updateStockStatus();
        inventoryRepository.save(inventory);

        response.put("success", true);
        response.put("message", "Copie empruntée");
        response.put("availableCopies", inventory.getAvailableCopies());
        response.put("totalCopies", inventory.getTotalCopies());

        System.out.println("📖 Emprunt: " + inventory.getBookTitle() +
                " - Restant: " + inventory.getAvailableCopies() + "/" + inventory.getTotalCopies());

        return response;
    }

    public Map<String, Object> returnBook(Long bookId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookId);

        if (optionalInventory.isEmpty()) {
            response.put("success", false);
            response.put("message", "Livre non trouvé dans l'inventaire");
            return response;
        }

        Inventory inventory = optionalInventory.get();

        if (inventory.getAvailableCopies() >= inventory.getTotalCopies()) {
            response.put("success", false);
            response.put("message", "Toutes les copies sont déjà disponibles");
            return response;
        }

        inventory.setAvailableCopies(inventory.getAvailableCopies() + 1);
        inventory.updateStockStatus();
        inventoryRepository.save(inventory);

        response.put("success", true);
        response.put("message", "Copie retournée");
        response.put("availableCopies", inventory.getAvailableCopies());
        response.put("totalCopies", inventory.getTotalCopies());

        System.out.println("✅ Retour: " + inventory.getBookTitle() +
                " - Disponible: " + inventory.getAvailableCopies() + "/" + inventory.getTotalCopies());

        return response;
    }

    public Map<String, Object> addCopies(Long bookId, int copies) {
        Map<String, Object> response = new HashMap<>();

        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookId);

        if (optionalInventory.isEmpty()) {
            response.put("success", false);
            response.put("message", "Livre non trouvé dans l'inventaire");
            return response;
        }

        Inventory inventory = optionalInventory.get();
        inventory.setTotalCopies(inventory.getTotalCopies() + copies);
        inventory.setAvailableCopies(inventory.getAvailableCopies() + copies);
        inventory.updateStockStatus();
        inventoryRepository.save(inventory);

        response.put("success", true);
        response.put("message", copies + " copies ajoutées");
        response.put("inventory", inventory);

        System.out.println("📦 Ajout: " + copies + " copies de " + inventory.getBookTitle());

        return response;
    }

    public Map<String, Object> reportDamaged(Long bookId, int damagedCount) {
        Map<String, Object> response = new HashMap<>();

        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookId);

        if (optionalInventory.isEmpty()) {
            response.put("success", false);
            response.put("message", "Livre non trouvé dans l'inventaire");
            return response;
        }

        Inventory inventory = optionalInventory.get();

        if (damagedCount > inventory.getAvailableCopies()) {
            response.put("success", false);
            response.put("message", "Nombre de copies endommagées supérieur aux copies disponibles");
            return response;
        }

        inventory.setAvailableCopies(inventory.getAvailableCopies() - damagedCount);
        inventory.setDamagedCopies(inventory.getDamagedCopies() + damagedCount);
        inventory.updateStockStatus();
        inventoryRepository.save(inventory);

        response.put("success", true);
        response.put("message", damagedCount + " copies marquées comme endommagées");
        response.put("inventory", inventory);

        return response;
    }

    public Map<String, Object> checkAvailability(Long bookId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookId);

        if (optionalInventory.isEmpty()) {
            response.put("available", false);
            response.put("message", "Livre non trouvé dans l'inventaire");
            return response;
        }

        Inventory inventory = optionalInventory.get();

        response.put("bookId", bookId);
        response.put("bookTitle", inventory.getBookTitle());
        response.put("available", inventory.isAvailable());
        response.put("availableCopies", inventory.getAvailableCopies());
        response.put("totalCopies", inventory.getTotalCopies());
        response.put("stockStatus", inventory.getStockStatus());
        response.put("location", inventory.getLocation());

        return response;
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        Integer totalBooks = inventoryRepository.getTotalBooksCount();
        Integer totalAvailable = inventoryRepository.getTotalAvailableCount();

        stats.put("totalBooks", totalBooks != null ? totalBooks : 0);
        stats.put("totalAvailable", totalAvailable != null ? totalAvailable : 0);
        stats.put("totalBorrowed", (totalBooks != null ? totalBooks : 0) - (totalAvailable != null ? totalAvailable : 0));
        stats.put("lowStockCount", inventoryRepository.findLowStock().size());
        stats.put("outOfStockCount", inventoryRepository.findByStockStatus(Inventory.StockStatus.OUT_OF_STOCK).size());

        return stats;
    }
}