package net.inventorymanagement.inventorymanagementwebservice.dtos;

import net.inventorymanagement.inventorymanagementwebservice.model.Department;
import net.inventorymanagement.inventorymanagementwebservice.model.Location;
import net.inventorymanagement.inventorymanagementwebservice.model.Supplier;
import net.inventorymanagement.inventorymanagementwebservice.model.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class InventoryItemDTO implements Comparable<InventoryItemDTO> {

    private String userName;
    private Integer id;
    private String itemInternalNumber;
    private Type type;
    private String itemName;
    private String serialNumber;
    private Supplier supplier;
    private Location location;
    private Integer pieces;
    private Integer piecesStored;
    private Integer piecesIssued;
    private Integer piecesDropped;
    private String issuedTo;
    private LocalDateTime deliveryDate;
    private LocalDateTime issueDate;
    private LocalDateTime droppingDate;
    private String status;
    private LocalDateTime creationDate;
    private LocalDateTime lastChangedDate;
    private boolean active;
    private String droppingQueue;
    private Department department;
    private String oldItemNumber;

    public InventoryItemDTO() {
    }

    public InventoryItemDTO(Integer id, String itemInternalNumber, Type type, String itemName,
                            String serialNumber, Supplier supplier, Location location,
                            Integer pieces, Integer piecesStored, Integer piecesIssued,
                            Integer piecesDropped, String issuedTo, LocalDateTime deliveryDate,
                            LocalDateTime issueDate, LocalDateTime droppingDate, String status,
                            LocalDateTime creationDate, LocalDateTime lastChangedDate, boolean active,
                            Department department, String oldItemNumber, String droppingQueue) {
        this.id = id;
        this.itemInternalNumber = itemInternalNumber;
        this.type = type;
        this.itemName = itemName;
        this.serialNumber = serialNumber;
        this.supplier = supplier;
        this.location = location;
        this.pieces = pieces;
        this.piecesStored = piecesStored;
        this.piecesIssued = piecesIssued;
        this.piecesDropped = piecesDropped;
        this.issuedTo = issuedTo;
        this.deliveryDate = deliveryDate;
        this.issueDate = issueDate;
        this.droppingDate = droppingDate;
        this.status = status;
        this.creationDate = creationDate;
        this.lastChangedDate = lastChangedDate;
        this.active = active;
        this.department = department;
        this.oldItemNumber = oldItemNumber;
        this.droppingQueue = droppingQueue;
    }

    public InventoryItemDTO(InventoryItemDTO dto) {
        this.userName = dto.getUserName();
        this.id = dto.getId();
        this.itemInternalNumber = dto.getItemInternalNumber();
        this.type = dto.getType();
        this.itemName = dto.getItemName();
        this.serialNumber = dto.getSerialNumber();
        this.supplier = dto.getSupplier();
        this.location = dto.getLocation();
        this.pieces = dto.getPieces();
        this.piecesStored = dto.getPiecesStored();
        this.piecesIssued = dto.getPiecesIssued();
        this.piecesDropped = dto.getPiecesDropped();
        this.issuedTo = dto.getIssuedTo();
        this.deliveryDate = dto.getDeliveryDate();
        this.issueDate = dto.getIssueDate();
        this.droppingDate = dto.getDroppingDate();
        this.status = dto.getStatus();
        this.creationDate = dto.getCreationDate();
        this.lastChangedDate = dto.getLastChangedDate();
        this.active = dto.isActive();
        this.department = dto.getDepartment();
        this.oldItemNumber = dto.getOldItemNumber();
        this.droppingQueue = dto.getDroppingQueue();
    }

    @Override
    public int compareTo(InventoryItemDTO o) {
        return this.getLastChangedDate().compareTo(o.getLastChangedDate());
    }

}
