package net.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "picture")
@Getter
@Setter
@ToString
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String pictureUrl;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "inventory_id")
    private InventoryItem inventoryItem;

}
