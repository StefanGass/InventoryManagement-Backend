package net.inventorymanagement.usermanagementwebservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "team")
@Getter
@ToString
public class Team implements Comparable<Team> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public Team() {
    }

    public Team(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(Team o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }

}

