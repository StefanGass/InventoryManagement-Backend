package net.inventorymanagement.usermanagementwebservice.service;

import net.inventorymanagement.usermanagementwebservice.model.User;
import net.inventorymanagement.usermanagementwebservice.repository.UserManagementRepository;
import net.inventorymanagement.usermanagementwebservice.utils.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User management rest service, responsible for all the program logic.
 */

@Service
public class UserManagementService {

    @Autowired
    private UserManagementRepository userManagementRepository;

    // GET or create one user, if it doesn't exist already
    // user data is created from login-name and might need additional manipulation
    public User getOneData(String username) {
        username = prepareUsername(username);
        String firstName = getFirstName(username);
        String lastName = getLastName(username);
        // change this to your extent
        String mailAddress = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@inventorymanagement.net";
        Integer userIndex = getUserIndex(firstName, lastName);
        if (userIndex != -1) {
            User user = userManagementRepository.getById(userIndex);
            user.setLastLogin(LocalDateTime.now());
            return userManagementRepository.save(user);
        } else {
            System.out.println("Created new user " + firstName + " " + lastName + ".");
            return userManagementRepository.save(new User(firstName, lastName, mailAddress, -1, false,
                    false, LocalDateTime.now(), true));
        }
    }

    // checks if user already exists inside database
    private Integer getUserIndex(String firstName, String lastName) {
        List<User> userList = userManagementRepository.findAll();
        for (int i = 0; i < userList.size(); i++) {
            for (User entry : userList) {
                if (Objects.equals(entry.getFirstName(), firstName) && Objects.equals(entry.getLastName(), lastName)) {
                    return entry.getId();
                }
            }
        }
        return -1;
    }

    // GET team
    // gets group id from user, checks if he/she is a teamleader and returns all members of his team if true
    public List<User> getTeamData(Integer id) {
        User teamLeader = userManagementRepository.getById(id);
        List<User> team = new ArrayList<>();
        List<User> allUsers = userManagementRepository.findAll();
        if (teamLeader.isTeamLeader()) {
            for (User entry : allUsers) {
                if (Objects.equals(entry.getGroupId(), teamLeader.getGroupId())) {
                    team.add(entry);
                }
            }
        }
        return team;
    }

    // GET all
    // checks if user is super admin and returns all users if true
    public List<User> getAllData(Integer id) {
        User superAdmin = userManagementRepository.getById(id);
        if (superAdmin.isSuperAdmin()) {
            return userManagementRepository.findAll();
        } else {
            return null;
        }
    }

    // additional manipulation of username for special usernames
    private String prepareUsername(String username) {
        username = username.replaceAll("\"", "").replaceAll(" ", "_");
        List<String> userExceptionsList = new ArrayList<>();
        FileReader fileReader = FileReader.getInstance();
        fileReader.loadFile(userExceptionsList);
        if (!userExceptionsList.isEmpty()) {
            for (String entry : userExceptionsList) {
                String[] userExceptionsEntry = entry.split(";");
                if (userExceptionsEntry[0].equalsIgnoreCase(username)) {
                    username = userExceptionsEntry[1];
                }
            }
        }
        username = replaceSpecialCharacters(username);
        return username;
    }

    // remove some special characters from the string
    private String replaceSpecialCharacters(String name) {
        name = name.toLowerCase()
                .replaceAll("ä", "ae").replaceAll("ö", "oe").replaceAll("ü", "ue")
                .replaceAll("á", "a").replaceAll("à", "a").replaceAll("â", "a")
                .replaceAll("é", "e").replaceAll("è", "e").replaceAll("ê", "e")
                .replaceAll("í", "i").replaceAll("ì", "i").replaceAll("î", "i")
                .replaceAll("ó", "o").replaceAll("ò", "o").replaceAll("ô", "o")
                .replaceAll("ú", "u").replaceAll("ù", "u").replaceAll("û", "u")
                .replaceAll("ß", "ss");
        return name;
    }

    // gets first part of the full name and formats it correctly
    private String getFirstName(String username) {
        String[] splitString = username.split("_");
        if (splitString[0].contains("-")) {
            String[] splitStringTwo = splitString[0].split("-");
            return splitStringTwo[0].substring(0, 1).toUpperCase() + splitStringTwo[0].substring(1).toLowerCase() + "-" +
                    splitStringTwo[1].substring(0, 1).toUpperCase() + splitStringTwo[1].substring(1).toLowerCase();
        } else {
            return splitString[0].substring(0, 1).toUpperCase() + splitString[0].substring(1).toLowerCase();
        }
    }

    // gets last part of the full name and formats it correctly
    private String getLastName(String username) {
        String[] splitString = username.split("_");
        if (splitString[splitString.length - 1].contains("-")) {
            String[] splitStringTwo = splitString[splitString.length - 1].split("-");
            return splitStringTwo[0].substring(0, 1).toUpperCase() + splitStringTwo[0].substring(1).toLowerCase() + "-" +
                    splitStringTwo[1].substring(0, 1).toUpperCase() + splitStringTwo[1].substring(1).toLowerCase();
        } else {
            return splitString[splitString.length - 1].substring(0, 1).toUpperCase() + splitString[splitString.length - 1].substring(1).toLowerCase();
        }
    }

}