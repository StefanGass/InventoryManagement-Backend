package net.inventorymanagement.inventorymanagementwebservice;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cloud.netflix.eureka.*;
import org.springframework.transaction.annotation.*;

/**
 * Inventory management webservice, responsible for creating and managing inventory data.
 */

@EnableEurekaClient
@SpringBootApplication
@EnableTransactionManagement
public class InventoryManagementWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementWebserviceApplication.class, args);
    }

}
