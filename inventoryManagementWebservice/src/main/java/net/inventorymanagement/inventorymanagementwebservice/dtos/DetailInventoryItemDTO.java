package net.inventorymanagement.inventorymanagementwebservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.inventorymanagement.inventorymanagementwebservice.model.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class DetailInventoryItemDTO extends InventoryItemDTO {

    private String droppingReason;
    private String comments;
    private List<Picture> pictures;
    private List<Change> change;

    // do not remove this
    public DetailInventoryItemDTO() {
    }

    // do not remove this
    public DetailInventoryItemDTO(Integer id, String itemInternalNumber, Type type, String itemName, String serialNumber, Supplier supplier, Location location, Integer pieces, Integer piecesStored, Integer piecesIssued, Integer piecesDropped, String issuedTo, LocalDateTime deliveryDate, LocalDateTime issueDate, LocalDateTime droppingDate, String droppingReason, String comments, String status, List<Picture> pictures, List<Change> change, boolean active, Department department, String oldItemNumber) {
        super(id, itemInternalNumber, type, itemName, serialNumber, supplier, location, pieces, piecesStored, piecesIssued, piecesDropped, issuedTo, deliveryDate, issueDate, droppingDate, status, null, active, department, oldItemNumber);
        this.droppingReason = droppingReason;
        this.comments = comments;
        this.pictures = pictures;
        this.change = change;
    }

    public DetailInventoryItemDTO(InventoryItemDTO dto, String droppingReason, String comments, List<Picture> pictures, List<Change> change) {
        super(dto);
        this.droppingReason = droppingReason;
        this.comments = comments;
        this.pictures = pictures;
        this.change = change;
    }

}
