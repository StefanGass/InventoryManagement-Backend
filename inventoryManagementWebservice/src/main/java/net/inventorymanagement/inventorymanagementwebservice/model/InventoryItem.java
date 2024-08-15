package net.inventorymanagement.inventorymanagementwebservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "inventory_item")
@Getter
@Setter
@ToString
public class InventoryItem implements Comparable<InventoryItem>, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String itemInternalNumber;
    @OneToOne
    @JoinColumn(name = "type_id")
    private Type type;
    private String itemName;
    private String serialNumber;
    private LocalDateTime warrantyEndDate;
    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;
    private String room;
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
    private String droppingQueue;
    private Integer droppingQueuePieces;
    private String droppingQueueReason;
    private Integer droppingQueueRequester;
    private LocalDateTime droppingQueueDate;
    @OneToOne
    @JoinColumn(name = "department_id")
    private Department department;
    private String oldItemNumber;

    public Change getFirstChange() {
        return this.change.stream().min(Comparator.comparing(Change::getChangeDate))
                .orElseThrow(IllegalArgumentException::new);
    }

    public Change getLastChange() {
        return this.change.stream().max(Comparator.comparing(Change::getChangeDate))
                .orElseThrow(IllegalArgumentException::new);
    }

    public InventoryItem clone() throws CloneNotSupportedException {
        return (InventoryItem) super.clone();
    }

    @Override
    public int compareTo(InventoryItem o) {
        return this.getItemInternalNumber().compareToIgnoreCase(o.getItemInternalNumber());
    }

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

    public String toString(int pictureCounter) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: {").append(id).append("}, ");
        sb.append("Inventarnummer: {").append(itemInternalNumber).append("}, ");
        sb.append("Kategorie: {").append(type.getCategory().getCategoryName()).append("}, ");
        sb.append("Typ: {").append(type.getTypeName()).append("}, ");
        sb.append("alte Inventarnummer: {").append(checkIfStringIsEmpty(oldItemNumber)).append("}, ");
        sb.append("Beschreibung: {").append(checkIfStringIsEmpty(itemName)).append("}, ");
        sb.append("Seriennummer: {").append(checkIfStringIsEmpty(serialNumber)).append("}, ");
        sb.append("Garantieablaufdatum: {").append(checkIfLocalDateTimeIsEmpty(warrantyEndDate)).append("}, ");
        sb.append("Abteilung: {").append(department.getDepartmentName()).append("}, ");
        sb.append("Standort: {").append(location.getLocationName()).append("}, ");
        sb.append("Raum: {").append(checkIfStringIsEmpty(room)).append("}, ");
        sb.append("Lieferant: {");
        if (supplier != null) {
            sb.append(supplier.getSupplierName());
        } else {
            sb.append("leer");
        }
        sb.append("}, ");
        sb.append("Lieferdatum: {").append(checkIfLocalDateTimeIsEmpty(deliveryDate)).append("}, ");
        sb.append("Stückzahl: {").append(pieces).append("}, ");
        sb.append("Stückzahl lagernd: {").append(piecesStored).append("}, ");
        sb.append("Stückzahl ausgegeben: {").append(piecesIssued).append("}, ");
        sb.append("Stückzahl ausgeschieden: {").append(piecesDropped).append("}, ");
        sb.append("ausgegeben an: {").append(checkIfStringIsEmpty(issuedTo)).append("}, ");
        sb.append("Ausgabedatum: {").append(checkIfLocalDateTimeIsEmpty(issueDate)).append("}, ");
        sb.append("Ausscheidedatum: {").append(checkIfLocalDateTimeIsEmpty(droppingDate)).append("}, ");
        sb.append("Ausscheidegrund: {").append(checkIfStringIsEmpty(droppingReason)).append("}, ");
        sb.append("Anmerkungen: {").append(checkIfStringIsEmpty(comments)).append("}, ");
        sb.append("Bilder: {").append(pictureCounter).append("}");
        return sb.toString();
    }

    private static String checkIfStringIsEmpty(String string) {
        if (string == null || string.isBlank()) {
            return "leer";
        } else {
            return string;
        }
    }

    private static String checkIfLocalDateTimeIsEmpty(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "leer";
        } else {
            return dateTime.toLocalDate().toString();
        }
    }

}
