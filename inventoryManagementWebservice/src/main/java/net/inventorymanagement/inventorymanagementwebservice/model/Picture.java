package net.inventorymanagement.inventorymanagementwebservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private String thumbnailUrl;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "inventory_id")
    private InventoryItem inventoryItem;

}
