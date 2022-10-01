package net.inventorymanagement.usercontrolwebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * User control rest webservice, responsible for authentication the users against our ldap server.
 * Active directory needed for it to work properly.
 */

@EnableEurekaClient
@SpringBootApplication
public class UserControlWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserControlWebserviceApplication.class, args);
    }

}
