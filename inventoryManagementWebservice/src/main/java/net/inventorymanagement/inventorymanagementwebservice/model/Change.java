package net.inventorymanagement.inventorymanagementwebservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_history")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Change implements Comparable<Change> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String user;
    private LocalDateTime changeDate;
    private String changeStatus;
    private String changeHistory;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "inventory_id")
    private InventoryItem inventoryItem;

    @Override
    public int compareTo(Change o) {
        return this.getChangeDate().compareTo(o.getChangeDate());
    }

}
