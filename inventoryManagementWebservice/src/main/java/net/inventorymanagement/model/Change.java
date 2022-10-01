package net.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "change_history")
@Getter
@Setter
@ToString
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String user;
    private LocalDateTime changeDate;
    private String changeStatus;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "inventory_id")
    private InventoryItem inventoryItem;

}
