package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    @Query(value = "SELECT * FROM inventory_item i WHERE i.id = :id", nativeQuery = true)
    InventoryItem findByItemId(int id);

    @Query(value = "SELECT * FROM inventory_item i WHERE i.department_id = :departmentId", nativeQuery = true)
    List<InventoryItem> findByDepartmentId(int departmentId);

    @Query(value = "SELECT * FROM inventory_item i WHERE i.item_internal_number = :itemInternalNumber", nativeQuery = true)
    InventoryItem findByItemInternalNumber(String itemInternalNumber);

    @Query(value = "SELECT i.* " +
            "FROM inventory_item i " +
            "JOIN `type` t ON i.type_id = t.id " +
            "JOIN category c ON t.category_id = c.id " +
            "JOIN supplier s ON i.supplier_id = s.id " +
            "JOIN `location` l ON i.location_id = l.id " +
            "JOIN department d ON i.department_id = d.id " +
            "WHERE " +
            "i.item_internal_number LIKE CONCAT('%', :search, '%') OR " +
            "i.item_name LIKE CONCAT('%', :search, '%') OR " +
            "i.serial_number LIKE CONCAT('%', :search, '%') OR " +
            "i.old_item_number LIKE CONCAT('%', :search, '%') OR " +
            "i.delivery_date LIKE CONCAT('%', :search, '%') OR " +
            "i.issued_to LIKE CONCAT('%', :search, '%') OR " +
            "i.issue_date LIKE CONCAT('%', :search, '%') OR " +
            "i.dropping_date LIKE CONCAT('%', :search, '%') OR " +
            "i.dropping_reason LIKE CONCAT('%', :search, '%') OR " +
            "i.comments LIKE CONCAT('%', :search, '%') OR " +
            "i.status LIKE CONCAT('%', :search, '%') OR " +
            "l.location_name LIKE CONCAT('%', :search, '%') OR " +
            "s.supplier_name LIKE CONCAT('%', :search, '%') OR " +
            "t.type_name LIKE CONCAT('%', :search, '%') OR " +
            "c.category_name LIKE CONCAT('%', :search, '%') OR " +
            "d.department_name LIKE CONCAT('%', :search, '%')",
            nativeQuery = true)
    List<InventoryItem> findBySearchString(String search);

    @Query(value = "SELECT i.* FROM inventory_item i " +
            "JOIN `type` t ON i.type_id = t.id " +
            "JOIN category c ON t.category_id = c.id " +
            "JOIN supplier s ON i.supplier_id = s.id " +
            "JOIN `location` l ON i.location_id = l.id " +
            "JOIN department d ON i.department_id = d.id " +
            "WHERE i.department_id = :departmentId AND (" +
            "i.item_internal_number LIKE CONCAT('%', :search, '%') OR " +
            "i.item_name LIKE CONCAT('%', :search, '%') OR " +
            "i.serial_number LIKE CONCAT('%', :search, '%') OR " +
            "i.old_item_number LIKE CONCAT('%', :search, '%') OR " +
            "i.delivery_date LIKE CONCAT('%', :search, '%') OR " +
            "i.issued_to LIKE CONCAT('%', :search, '%') OR " +
            "i.issue_date LIKE CONCAT('%', :search, '%') OR " +
            "i.dropping_date LIKE CONCAT('%', :search, '%') OR " +
            "i.dropping_reason LIKE CONCAT('%', :search, '%') OR " +
            "i.comments LIKE CONCAT('%', :search, '%') OR " +
            "i.status LIKE CONCAT('%', :search, '%') OR " +
            "l.location_name LIKE CONCAT('%', :search, '%') OR " +
            "s.supplier_name LIKE CONCAT('%', :search, '%') OR " +
            "t.type_name LIKE CONCAT('%', :search, '%') OR " +
            "c.category_name LIKE CONCAT('%', :search, '%') OR " +
            "d.department_name LIKE CONCAT('%', :search, '%'))  ",
            nativeQuery = true)
    List<InventoryItem> findBySearchStringAndDepartmentId(String search, int departmentId);

    @Query(value = "SELECT i from InventoryItem i " +
            "LEFT JOIN i.change ch " +
            "WHERE " +
            "(:departmentId IS NULL OR i .department.id = :departmentId) AND " +
            "(:typeId IS NULL OR i .type.id = :typeId ) AND " +
            "(:categoryId IS NULL OR i .type.category.id = :categoryId) AND " +
            "(:locationId IS NULL OR i .location.id = :locationId) AND " +
            "(:supplierId IS NULL OR i .supplier.id = :supplierId) AND " +
            "(:status IS NULL OR i .status = :status) AND " +
            "ch.changeStatus='Inventargegenstand angelegt.' AND " +
            "(:changeDateFrom IS NULL OR ch.changeDate >= :changeDateFrom) AND " +
            "(:changeDateTo IS NULL OR ch.changeDate <= :changeDateTo) AND " +
            "(:deliveryDateFrom IS NULL OR i .deliveryDate >= :deliveryDateFrom) AND " +
            "(:deliveryDateTo IS NULL OR i .deliveryDate <= :deliveryDateTo) AND " +
            "(:issueDateFrom IS NULL OR i .issueDate >= :issueDateFrom) AND " +
            "(:issueDateTo IS NULL OR i .issueDate <= :issueDateTo ) AND " +
            "(:droppingDateFrom IS NULL OR i .droppingDate >= :droppingDateFrom ) AND " +
            "(:droppingDateTo IS NULL OR i .droppingDate <= :droppingDateTo )")
    List<InventoryItem> findByOptionalParameters(Integer departmentId, Integer categoryId, Integer typeId,
                                                 Integer locationId, Integer supplierId, String status,
                                                 LocalDateTime changeDateFrom, LocalDateTime changeDateTo,
                                                 LocalDateTime deliveryDateFrom, LocalDateTime deliveryDateTo,
                                                 LocalDateTime issueDateFrom, LocalDateTime issueDateTo,
                                                 LocalDateTime droppingDateFrom, LocalDateTime droppingDateTo);

}
