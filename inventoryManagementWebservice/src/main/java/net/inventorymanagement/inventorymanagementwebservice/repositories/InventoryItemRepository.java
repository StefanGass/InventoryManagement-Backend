package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    @Query(value = "SELECT * FROM inventory_item i WHERE i.id = :id", nativeQuery = true)
    InventoryItem findByItemId(Integer id);

    @Query(value = "SELECT * FROM inventory_item i WHERE i.department_id = :departmentId", nativeQuery = true)
    List<InventoryItem> findByDepartmentId(Integer departmentId);

    @Query(value = "SELECT * FROM inventory_item i WHERE i.item_internal_number = :itemInternalNumber", nativeQuery = true)
    InventoryItem findByItemInternalNumber(String itemInternalNumber);

}
