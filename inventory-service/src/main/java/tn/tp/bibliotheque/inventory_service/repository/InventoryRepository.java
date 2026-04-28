package tn.tp.bibliotheque.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.tp.bibliotheque.inventory_service.entity.Inventory;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByBookId(Long bookId);

    List<Inventory> findByStockStatus(Inventory.StockStatus status);

    List<Inventory> findByAvailableCopiesGreaterThan(int copies);

    List<Inventory> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT i FROM Inventory i WHERE i.availableCopies > 0")
    List<Inventory> findAllAvailable();

    @Query("SELECT i FROM Inventory i WHERE i.availableCopies <= 2 AND i.availableCopies > 0")
    List<Inventory> findLowStock();

    @Query("SELECT SUM(i.totalCopies) FROM Inventory i")
    Integer getTotalBooksCount();

    @Query("SELECT SUM(i.availableCopies) FROM Inventory i")
    Integer getTotalAvailableCount();

    boolean existsByBookId(Long bookId);
}