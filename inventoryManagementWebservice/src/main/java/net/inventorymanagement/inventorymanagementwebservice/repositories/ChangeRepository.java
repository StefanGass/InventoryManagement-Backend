package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChangeRepository extends JpaRepository<Change, Integer> {

    @Query(value = "SELECT * FROM change_history c WHERE c.change_date >= :fromDate AND c.change_date <= :toDate", nativeQuery = true)
    List<Change> findByDateRange(LocalDateTime fromDate, LocalDateTime toDate);

}
