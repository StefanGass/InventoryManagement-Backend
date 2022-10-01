package net.inventorymanagement.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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
