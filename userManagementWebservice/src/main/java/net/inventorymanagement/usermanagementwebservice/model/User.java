package net.inventorymanagement.usermanagementwebservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * User management model, defines user parameters - 1:1 mirrored to database.
 */

@Entity
@Getter
@Setter
public class User implements Comparable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String mailAddress;
    private Integer groupId;
    private boolean teamLeader;
    private boolean admin;
    private boolean superAdmin;
    private LocalDateTime lastLogin;
    private boolean active;

    public User() {
    }

    public User(String firstName, String lastName, String mailAddress, Integer groupId, boolean teamLeader,
                boolean admin, boolean superAdmin, LocalDateTime lastLogin, boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mailAddress = mailAddress;
        this.groupId = groupId;
        this.teamLeader = teamLeader;
        this.admin = admin;
        this.superAdmin = superAdmin;
        this.lastLogin = lastLogin;
        this.active = active;
    }

    @Override
    public int compareTo(User o) {
        return this.getLastName().compareToIgnoreCase(o.getLastName());
    }

}

