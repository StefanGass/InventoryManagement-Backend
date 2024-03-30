package net.inventorymanagement.usermanagementwebservice.controller;

import net.inventorymanagement.usermanagementwebservice.dto.UserNameDTO;
import net.inventorymanagement.usermanagementwebservice.model.User;
import net.inventorymanagement.usermanagementwebservice.service.UserManagementService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * User management rest controller, responsible for mapping requests from the frontend.
 */

@RestController
@RequestMapping("api/usermanagement")
@Log4j2
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    // GET one user
    // data is additionally base64 encoded and has to be decoded manually
    @GetMapping(path = "user/{encodedUsername}")
    public User getUserData(@PathVariable String encodedUsername) throws Exception {
        byte[] decoded = Base64.decodeBase64(encodedUsername);
        return userManagementService.getUserData(new String(decoded, StandardCharsets.UTF_8));
    }

    @GetMapping(path = "activeDirectory/all")
    public List<User> getAllActiveDirectoryUserData() throws Exception {
        return userManagementService.getAllActiveDirectoryUserData();
    }

    // GET all user data
    @GetMapping(path = "admin/{id}")
    public List<User> getAllUsersData(@PathVariable int id) {
        return userManagementService.getAllUsersData(id);
    }

    // GET username
    @GetMapping(path = "name/{id}")
    public UserNameDTO getUsername(@PathVariable int id) {
        return userManagementService.getUsername(id);
    }

    // GET all usernames
    @GetMapping(path = "name/all")
    public List<UserNameDTO> getAllUsernames() {
        return userManagementService.getAllUsernames(false);
    }

    // GET all usernames from active users only
    @GetMapping(path = "name/all/active")
    public List<UserNameDTO> getAllActiveUsernames() {
        return userManagementService.getAllUsernames(true);
    }

    // GET username from list
    @PostMapping(path = "name/list")
    public List<UserNameDTO> getUsernamesFromList(@RequestBody List<UserNameDTO> userNameDTOList) {
        return userManagementService.getUsernameFromList(userNameDTOList);
    }

}