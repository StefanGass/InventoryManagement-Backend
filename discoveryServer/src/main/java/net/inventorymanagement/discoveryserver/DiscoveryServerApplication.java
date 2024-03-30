package net.inventorymanagement.discoveryserver;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Discovery server, discovers all the microservice instances and communicates them to the gateway.
 */

@SpringBootApplication
@EnableEurekaServer
@Log4j2
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }

}
