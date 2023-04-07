package net.inventorymanagement.inventorymanagementwebservice.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.search.annotations.*;

@Entity
@Table(name = "type")
@Getter
@Setter
@ToString
@Indexed
public class Type implements Comparable<Type> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Field
    private String typeName;
    @OneToOne
    @JoinColumn(name = "category_id")
    @IndexedEmbedded
    private Category category;

    @Override
    public int compareTo(Type o) {
        return this.getTypeName().compareToIgnoreCase(o.getTypeName());
    }

}
