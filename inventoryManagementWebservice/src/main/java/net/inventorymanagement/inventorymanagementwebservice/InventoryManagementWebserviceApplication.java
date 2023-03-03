package net.inventorymanagement.inventorymanagementwebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Inventory management webservice, responsible for creating and managing inventory data.
 */

@EnableEurekaClient
@SpringBootApplication
public class InventoryManagementWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementWebserviceApplication.class, args);
    }

}
