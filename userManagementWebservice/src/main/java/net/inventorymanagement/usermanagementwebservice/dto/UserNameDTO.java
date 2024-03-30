package net.inventorymanagement.usermanagementwebservice.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for transferring only the usernames.
 */

@Getter
@Setter
public class UserNameDTO implements Comparable<UserNameDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public UserNameDTO() {
    }

    public UserNameDTO(Integer id, String firstName, String lastName) {
        this.id = id;
        this.name = firstName + " " + lastName;
    }

    @Override
    public int compareTo(UserNameDTO o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }

}

