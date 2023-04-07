package net.inventorymanagement.inventorymanagementwebservice.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.search.annotations.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@ToString
@Indexed
public class Category implements Comparable<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Field
    private String categoryName;
    private String prefix;

    @Override
    public int compareTo(Category o) {
        return this.getCategoryName().compareToIgnoreCase(o.getCategoryName());
    }

}
