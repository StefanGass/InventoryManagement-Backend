package net.inventorymanagement.inventorymanagementwebservice.model;

import com.fasterxml.jackson.annotation.*;
import java.time.*;
import java.util.*;
import javax.persistence.*;
import lombok.*;
import org.apache.lucene.analysis.core.*;
import org.hibernate.search.annotations.*;

@Entity
@Table(name = "inventory_item")
@Getter
@Setter
@ToString
@Indexed
@AnalyzerDef(name = "itemInternalNumberAnalyzer",
    tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),
    filters = {@TokenFilterDef(factory = LowerCaseFilterFactory.class)}
)
public class InventoryItem implements Comparable<InventoryItem>, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Field(analyzer = @Analyzer(definition = "itemInternalNumberAnalyzer"))
    private String itemInternalNumber;
    @OneToOne
    @JoinColumn(name = "type_id")
    @IndexedEmbedded
    private Type type;
    @Field
    private String itemName;
    @Field
    private String serialNumber;
    @OneToOne
    @JoinColumn(name = "supplier_id")
    @IndexedEmbedded
    private Supplier supplier;
    @OneToOne
    @JoinColumn(name = "location_id")
    @IndexedEmbedded
    private Location location;
    private Integer pieces;
    private Integer piecesStored;
    private Integer piecesIssued;
    private Integer piecesDropped;
    @Field
    private String issuedTo;
    private LocalDateTime deliveryDate;

    private LocalDateTime issueDate;
    private LocalDateTime droppingDate;
    @Field
    private String droppingReason;
    @Field
    private String comments;
    @Field
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
    private boolean droppingQueue;
    @OneToOne
    @JoinColumn(name = "department_id")
    @IndexedEmbedded
    private Department department;
    @Field
    private String oldItemNumber;

    public InventoryItem clone() throws CloneNotSupportedException {
        return (InventoryItem) super.clone();
    }

    @Override
    public int compareTo(InventoryItem o) {
        return this.getItemInternalNumber().compareToIgnoreCase(o.getItemInternalNumber());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
        sb.append("Typ: {").append(type.getTypeName()).append("}, ");
        sb.append("Inventarnummer: {").append(itemInternalNumber).append("}, ");
        sb.append("Beschreibung: {").append(checkIfEmpty(itemName)).append("}, ");
        sb.append("Seriennummer: {").append(checkIfEmpty(serialNumber)).append("}, ");
        sb.append("Standort: {").append(location.getLocationName()).append("}, ");
        sb.append("Abteilung: {").append(department.getDepartmentName()).append("}, ");
        sb.append("Lieferant: {");
        if (supplier != null) {
            sb.append(supplier.getSupplierName());
        } else {
            sb.append("leer");
        }
        sb.append("}, ");
        sb.append("Lieferdatum: {");
        if (deliveryDate != null) {
            sb.append(deliveryDate.toLocalDate());
        } else {
            sb.append("leer");
        }
        sb.append("}, ");
        sb.append("Stückzahl: {").append(pieces).append("}, ");
        sb.append("Stückzahl lagernd: {").append(piecesStored).append("}, ");
        sb.append("Stückzahl ausgegeben: {").append(piecesIssued).append("}, ");
        sb.append("Stückzahl ausgeschieden: {").append(piecesDropped).append("}, ");
        sb.append("ausgegeben an: {").append(checkIfEmpty(issuedTo)).append("}, ");
        sb.append("Ausgabedatum: {");
        if (issueDate != null) {
            sb.append(issueDate.toLocalDate());
        } else {
            sb.append("leer");
        }
        sb.append("}, ");
        sb.append("Ausscheidedatum: {");
        if (droppingDate != null) {
            sb.append(droppingDate.toLocalDate());
        } else {
            sb.append("leer");
        }
        sb.append("}, ");
        sb.append("Ausscheidegrund: {").append(checkIfEmpty(droppingReason)).append("}, ");
        sb.append("alte Inventarnummer: {").append(checkIfEmpty(oldItemNumber)).append("}, ");
        sb.append("Anmerkungen: {").append(checkIfEmpty(comments)).append("}, ");
        sb.append("Bilder: {").append(pictureCounter).append("}");
        return sb.toString();
    }

    private static String checkIfEmpty(String string) {
        if (string == null || string.equals("")) {
            return "leer";
        } else {
            return string;
        }
    }

}
