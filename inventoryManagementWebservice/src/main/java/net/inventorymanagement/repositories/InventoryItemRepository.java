package net.inventorymanagement.repositories;

import net.inventorymanagement.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    @Query(value = "SELECT * FROM inventory_item i WHERE i.department_id = :departmentId", nativeQuery = true)
    List<InventoryItem> findByDepartmentId(Integer departmentId);

    @Query(value = "SELECT * FROM inventory_item inu WHERE inu.item_internal_number = :itemInternalNumber", nativeQuery = true)
    InventoryItem findByItemInternalNumber(String itemInternalNumber);

}
