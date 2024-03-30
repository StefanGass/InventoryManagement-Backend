package net.inventorymanagement.usermanagementwebservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * User management model, defines user parameters - 1:1 mirrored to database.
 */

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString
public class User implements Comparable<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userLogonName;
    private String firstName;
    private String lastName;
    private String mailAddress;
    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;
    private boolean teamLeader;
    private boolean admin;
    private boolean superAdmin;
    private LocalDateTime lastLogin;
    private boolean active;
    private boolean authUserManagement;
    private boolean authInventoryManagement;

    public User() {
    }

    public User(String userLogonName, String firstName, String lastName, String mailAddress, Team team, boolean teamLeader, boolean admin, boolean superAdmin, LocalDateTime lastLogin, boolean active, boolean authUserManagement, boolean authInventoryManagement) {
        this.userLogonName = userLogonName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mailAddress = mailAddress;
        this.team = team;
        this.teamLeader = teamLeader;
        this.admin = admin;
        this.superAdmin = superAdmin;
        this.lastLogin = lastLogin;
        this.active = active;
        this.authUserManagement = authUserManagement;
        this.authInventoryManagement = authInventoryManagement;
    }

    public User(String userLogonName, String firstName, String lastName, String mailAddress, boolean active) {
        this.userLogonName = userLogonName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mailAddress = mailAddress;
        this.active = active;
    }

    @Override
    public int compareTo(User o) {
        return this.getLastName().compareToIgnoreCase(o.getLastName());
    }

}

