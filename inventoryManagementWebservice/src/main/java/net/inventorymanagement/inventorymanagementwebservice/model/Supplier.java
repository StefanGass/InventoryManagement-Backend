package net.inventorymanagement.inventorymanagementwebservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@ToString
public class Supplier implements Comparable<Supplier> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String supplierName;
    private String link;

    @Override
    public int compareTo(Supplier o) {
        return this.getSupplierName().compareToIgnoreCase(o.getSupplierName());
    }

}
