package net.inventorymanagement.inventorymanagementwebservice.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.search.annotations.*;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@ToString
@Indexed
public class Supplier implements Comparable<Supplier> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Field
    private String supplierName;
    private String link;

    @Override
    public int compareTo(Supplier o) {
        return this.getSupplierName().compareToIgnoreCase(o.getSupplierName());
    }

}
