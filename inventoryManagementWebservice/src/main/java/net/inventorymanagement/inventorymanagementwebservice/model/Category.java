package net.inventorymanagement.inventorymanagementwebservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "category")
@Getter
@Setter
@ToString
public class Category implements Comparable<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String categoryName;
    private String prefix;

    @Override
    public int compareTo(Category o) {
        return this.getCategoryName().compareToIgnoreCase(o.getCategoryName());
    }

}
