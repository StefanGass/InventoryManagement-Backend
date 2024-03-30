package net.inventorymanagement.apigatewayloadbalancer;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API gateway, takes all API calls and routes them to the appropriate microservices.
 * Load balancing is done automatically.
 */

@SpringBootApplication
@Log4j2
public class ApiGatewayLoadBalancerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayLoadBalancerApplication.class, args);
    }

}
