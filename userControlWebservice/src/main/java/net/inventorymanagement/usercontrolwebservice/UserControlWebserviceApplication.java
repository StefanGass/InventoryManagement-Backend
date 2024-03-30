package net.inventorymanagement.usercontrolwebservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * User control rest webservice, responsible for authentication the users against our ldap server.
 * Active directory needed for it to work properly.
 */

@SpringBootApplication
@Log4j2
public class UserControlWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserControlWebserviceApplication.class, args);
    }

}
