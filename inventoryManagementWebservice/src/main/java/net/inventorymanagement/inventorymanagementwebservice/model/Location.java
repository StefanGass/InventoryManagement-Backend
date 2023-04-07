package net.inventorymanagement.inventorymanagementwebservice.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.search.annotations.*;

@Entity
@Table(name = "location")
@Getter
@Setter
@ToString
@Indexed
public class Location implements Comparable<Location> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Field
    private String locationName;

    @Override
    public int compareTo(Location o) {
        return this.getLocationName().compareToIgnoreCase(o.getLocationName());
    }

}
