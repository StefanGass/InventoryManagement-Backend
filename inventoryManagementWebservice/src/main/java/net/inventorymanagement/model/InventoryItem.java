package net.inventorymanagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "inventory_item")
@Getter
@Setter
@ToString
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String itemInternalNumber;
    @OneToOne
    @JoinColumn(name = "type_id")
    private Type type;
    private String itemName;
    private String serialNumber;
    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private Integer pieces;
    private Integer piecesStored;
    private Integer piecesIssued;
    private Integer piecesDropped;
    private String issuedTo;
    private LocalDateTime deliveryDate;
    private LocalDateTime issueDate;
    private LocalDateTime droppingDate;
    private String droppingReason;
    private String comments;
    private String status;
    @OneToMany(mappedBy = "inventoryItem")
    @JsonManagedReference
    @ToString.Exclude
    private List<Picture> pictures;
    @OneToMany(mappedBy = "inventoryItem")
    @JsonManagedReference
    @ToString.Exclude
    private List<Change> change;
    private boolean active;
    @OneToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return itemInternalNumber.equals(that.itemInternalNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemInternalNumber);
    }
}
