package net.inventorymanagement.usercontrolwebservice.controller;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User control rest configuration, responsible for mapping requests from the frontend.
 */

@EnableEurekaClient
@RestController
@RequestMapping(path = "/api/usercontrol")
public class UserControlController {

    // string is shown when service is accessed directly via browser and login was possible...
    @GetMapping
    public String index() {
        return "Nothing to see here...";
    }

}