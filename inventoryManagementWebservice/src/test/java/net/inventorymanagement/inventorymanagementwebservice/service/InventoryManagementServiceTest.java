package net.inventorymanagement.inventorymanagementwebservice.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Example Test to demonstrate that Liquibase sets up an in memory H2 database and fills it
 * with example data located in src/test/resources/config/liquibase/example_data.sql
 */
@SpringBootTest
class InventoryManagementServiceTest {

    @Autowired
    InventoryManagementService inventoryManagementService;

    @Test
    public void shouldGetAllInventoryItems() {
        List<InventoryItem> allItems = inventoryManagementService.getAllInventoryItems();
        assertEquals(1, allItems.size());
    }
}