package net.inventorymanagement.apigatewayloadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * API gateway, takes all API calls and routes them to the appropriate microservices.
 * Load balancing is done automatically.
 */
@EnableEurekaClient
@SpringBootApplication
public class ApiGatewayLoadBalancerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayLoadBalancerApplication.class, args);
    }

}
