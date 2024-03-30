package net.inventorymanagement.usermanagementwebservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User management webservice, responsible for creating and managing user data.
 */

@SpringBootApplication
@Log4j2
public class UserManagementWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementWebserviceApplication.class, args);
    }

}
