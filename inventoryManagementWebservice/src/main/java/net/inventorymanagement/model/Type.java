package net.inventorymanagement.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "type")
@Getter
@Setter
@ToString
public class Type implements Comparable<Type> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String typeName;
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public int compareTo(Type o) {
        return this.getTypeName().compareToIgnoreCase(o.getTypeName());
    }

}
