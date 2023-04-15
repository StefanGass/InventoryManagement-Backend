package net.inventorymanagement.usermanagementwebservice.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.inventorymanagement.usermanagementwebservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Example Test to demonstrate that Liquibase sets up an in memory H2 database and fills it
 * with example data located in src/test/resources/config/liquibase/example_data.sql
 */
@SpringBootTest
class UserManagementServiceTest {

    @Autowired
    UserManagementService userManagementService;

    @Test
    public void shouldGetAllData() {
        List<User> allItems = userManagementService.getAllData(1);
        assertEquals(1, allItems.size());
    }
}