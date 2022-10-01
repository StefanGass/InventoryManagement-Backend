package net.inventorymanagement.usermanagementwebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * User management webservice, responsible for creating and managing user data.
 */

@EnableEurekaClient
@SpringBootApplication
public class UserManagementWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementWebserviceApplication.class, args);
    }

}
