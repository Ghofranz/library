package tn.tp.bibliotheque.inventory_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tp.bibliotheque.inventory_service.entity.Inventory;
import tn.tp.bibliotheque.inventory_service.service.InventoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // GET - Tout l'inventaire
    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    // GET - Inventaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Inventaire par bookId
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Inventory> getInventoryByBookId(@PathVariable Long bookId) {
        return inventoryService.getInventoryByBookId(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET - Livres disponibles
    @GetMapping("/available")
    public List<Inventory> getAvailableInventory() {
        return inventoryService.getAvailableInventory();
    }

    // GET - Stock faible
    @GetMapping("/low-stock")
    public List<Inventory> getLowStockInventory() {
        return inventoryService.getLowStockInventory();
    }

    // GET - Rupture de stock
    @GetMapping("/out-of-stock")
    public List<Inventory> getOutOfStockInventory() {
        return inventoryService.getOutOfStockInventory();
    }

    // GET - Vérifier disponibilité (public pour loan-service)
    @GetMapping("/public/check/{bookId}")
    public ResponseEntity<Map<String, Object>> checkAvailability(@PathVariable Long bookId) {
        return ResponseEntity.ok(inventoryService.checkAvailability(bookId));
    }

    // GET - Statistiques
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(inventoryService.getStatistics());
    }

    // POST - Créer inventaire
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInventory(@RequestBody Inventory inventory) {
        Map<String, Object> response = inventoryService.createInventory(inventory);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // POST - Emprunter (appelé par loan-service)
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<Map<String, Object>> borrowBook(@PathVariable Long bookId) {
        Map<String, Object> response = inventoryService.borrowBook(bookId);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // POST - Retourner (appelé par loan-service)
    @PostMapping("/return/{bookId}")
    public ResponseEntity<Map<String, Object>> returnBook(@PathVariable Long bookId) {
        Map<String, Object> response = inventoryService.returnBook(bookId);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // POST - Ajouter des copies
    @PostMapping("/add-copies/{bookId}")
    public ResponseEntity<Map<String, Object>> addCopies(
            @PathVariable Long bookId,
            @RequestBody Map<String, Integer> body) {
        int copies = body.get("copies");
        Map<String, Object> response = inventoryService.addCopies(bookId, copies);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // POST - Signaler copies endommagées
    @PostMapping("/damaged/{bookId}")
    public ResponseEntity<Map<String, Object>> reportDamaged(
            @PathVariable Long bookId,
            @RequestBody Map<String, Integer> body) {
        int damaged = body.get("damaged");
        Map<String, Object> response = inventoryService.reportDamaged(bookId, damaged);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}