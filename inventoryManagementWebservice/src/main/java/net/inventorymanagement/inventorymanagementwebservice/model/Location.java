package net.inventorymanagement.inventorymanagementwebservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "location")
@Getter
@Setter
@ToString
public class Location implements Comparable<Location> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String locationName;

    @Override
    public int compareTo(Location o) {
        return this.getLocationName().compareToIgnoreCase(o.getLocationName());
    }

}
