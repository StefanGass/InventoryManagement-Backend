package net.inventorymanagement.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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

    @Override
    public int compareTo(Category o) {
        return this.getCategoryName().compareToIgnoreCase(o.getCategoryName());
    }

}
