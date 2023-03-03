package net.inventorymanagement.usermanagementwebservice.controller;

import net.inventorymanagement.usermanagementwebservice.model.User;
import net.inventorymanagement.usermanagementwebservice.service.UserManagementService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * User management rest controller, responsible for mapping requests from the frontend.
 */

@EnableEurekaClient
@RestController
@RequestMapping("api/usermanagement")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    // GET one user
    // data is additionally base64 encoded and has to be decoded manually
    @GetMapping(path = "user/{username}")
    public User getData(@PathVariable("username") String encodedUsername) {
        byte[] decoded = Base64.decodeBase64(encodedUsername);
        return userManagementService.getOneData(new String(decoded, StandardCharsets.UTF_8));
    }

    // GET team
    @GetMapping(path = "team/{id}")
    public List<User> getTeamData(@PathVariable("id") Integer id) {
        return userManagementService.getTeamData(id);
    }

    // GET all
    @GetMapping(path = "admin/{id}")
    public List<User> getAllData(@PathVariable("id") Integer id) {
        return userManagementService.getAllData(id);
    }

}