package net.inventorymanagement.inventorymanagementwebservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Inventory management webservice, responsible for creating and managing inventory data.
 */

@SpringBootApplication
@EnableTransactionManagement
@Log4j2
public class InventoryManagementWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementWebserviceApplication.class, args);
    }

}
