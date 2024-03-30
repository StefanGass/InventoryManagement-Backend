package net.inventorymanagement.inventorymanagementwebservice.utils;

import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.Objects;

@Log4j2
public class ItemComparator {

    public static String getChangeString(InventoryItem originalItem, InventoryItem newItem, int pictureCounter) {
        StringBuilder sb = new StringBuilder();

        if (!originalItem.getPiecesDropped().equals(newItem.getPiecesDropped())) {
            // dropping parameters will only be changed if someone approves it, so this does not need additional verification
            sb.append("Ausscheidung bestätigt: ");
        }

            if (!originalItem.getItemName().equals(newItem.getItemName())) {
            sb.append("Beschreibung: {");
            sb.append(checkIfEmpty(originalItem.getItemName()));
            sb.append("} -> {");
            sb.append(checkIfEmpty(newItem.getItemName()));
            sb.append("}, ");
        }
        if (!originalItem.getOldItemNumber().equals(newItem.getOldItemNumber())) {
            if (!sb.isEmpty()) {
                sb.append("alte Inventarnummer: {");
            } else {
                sb.append("Alte Inventarnummer {");
            }
            sb.append(originalItem.getOldItemNumber());
            sb.append("} -> {");
            sb.append(newItem.getOldItemNumber());
            sb.append("}, ");
        }
        if (!originalItem.getSerialNumber().equals(newItem.getSerialNumber())) {
            sb.append("Seriennummer: {");
            sb.append(checkIfEmpty(originalItem.getSerialNumber()));
            sb.append("} -> {");
            sb.append(checkIfEmpty(newItem.getSerialNumber()));
            sb.append("}, ");
        }
        if (!originalItem.getLocation().equals(newItem.getLocation())) {
            sb.append("Standort: {");
            sb.append(originalItem.getLocation().getLocationName());
            sb.append("} -> {");
            sb.append(newItem.getLocation().getLocationName());
            sb.append("}, ");
        }
        if (!originalItem.getDepartment().equals(newItem.getDepartment())) {
            sb.append("Abteilung: {");
            sb.append(originalItem.getDepartment().getDepartmentName());
            sb.append("} -> {");
            sb.append(newItem.getDepartment().getDepartmentName());
            sb.append("}, ");
        }
        if (!originalItem.getSupplier().equals(newItem.getSupplier())) {
            sb.append("Lieferant: {");
            if (originalItem.getSupplier() != null) {
                sb.append(originalItem.getSupplier().getSupplierName());
            } else {
                sb.append("leer");
            }
            sb.append("} -> {");
            if (newItem.getSupplier() != null) {
                sb.append(newItem.getSupplier().getSupplierName());
            } else {
                sb.append("leer");
            }
            sb.append("}, ");
        }
        if (originalItem.getDeliveryDate() != null && newItem.getDeliveryDate() != null) {
            if (!originalItem.getDeliveryDate().toLocalDate().isEqual(newItem.getDeliveryDate().toLocalDate())) {
                getDateString("Lieferdatum:", sb, originalItem.getDeliveryDate().toLocalDate(), newItem.getDeliveryDate().toLocalDate());
            }
        } else if (originalItem.getDeliveryDate() != null) {
            getDateString("Lieferdatum:", sb, originalItem.getDeliveryDate().toLocalDate(), null);
        } else if (newItem.getDeliveryDate() != null) {
            getDateString("Lieferdatum:", sb, null, newItem.getDeliveryDate().toLocalDate());
        }
        if (!originalItem.getPieces().equals(newItem.getPieces())) {
            sb.append("Stückzahl: {");
            sb.append(originalItem.getPieces());
            sb.append("} -> {");
            sb.append(newItem.getPieces());
            sb.append("}, ");
        }
        if (!originalItem.getPiecesStored().equals(newItem.getPiecesStored())) {
            sb.append("Stückzahl lagernd: {");
            sb.append(originalItem.getPiecesStored());
            sb.append("} -> {");
            sb.append(newItem.getPiecesStored());
            sb.append("}, ");
        }
        if (!originalItem.getPiecesIssued().equals(newItem.getPiecesIssued())) {
            sb.append("Stückzahl ausgegeben: {");
            sb.append(originalItem.getPiecesIssued());
            sb.append("} -> {");
            sb.append(newItem.getPiecesIssued());
            sb.append("}, ");
        }
        if (!originalItem.getPiecesDropped().equals(newItem.getPiecesDropped())) {
            sb.append("Stückzahl ausgeschieden: {");
            sb.append(originalItem.getPiecesDropped());
            sb.append("} -> {");
            sb.append(newItem.getPiecesDropped());
            sb.append("}, ");
        }
        if (!originalItem.getIssuedTo().equals(newItem.getIssuedTo())) {
            if (!sb.isEmpty()) {
                sb.append("ausgegeben an: {");
            } else {
                sb.append("Ausgegeben an: {");
            }
            sb.append(checkIfEmpty(originalItem.getIssuedTo()));
            sb.append("} -> {");
            sb.append(checkIfEmpty(newItem.getIssuedTo()));
            sb.append("}, ");
        }
        if (originalItem.getIssueDate() != null && newItem.getIssueDate() != null) {
            if (!originalItem.getIssueDate().toLocalDate().isEqual(newItem.getIssueDate().toLocalDate())) {
                getDateString("Ausgabedatum:", sb, originalItem.getIssueDate().toLocalDate(), newItem.getIssueDate().toLocalDate());
            }
        } else if (originalItem.getIssueDate() != null) {
            getDateString("Ausgabedatum:", sb, originalItem.getIssueDate().toLocalDate(), null);
        } else if (newItem.getIssueDate() != null) {
            getDateString("Ausgabedatum:", sb, null, newItem.getIssueDate().toLocalDate());
        }
        if (originalItem.getDroppingDate() != null && newItem.getDroppingDate() != null) {
            if (!originalItem.getDroppingDate().toLocalDate().isEqual(newItem.getDroppingDate().toLocalDate())) {
                getDateString("Ausscheidedatum:", sb, originalItem.getDroppingDate().toLocalDate(), newItem.getDroppingDate().toLocalDate());
            }
        } else if (originalItem.getDroppingDate() != null) {
            getDateString("Ausscheidedatum:", sb, originalItem.getDroppingDate().toLocalDate(), null);
        } else if (newItem.getDroppingDate() != null) {
            getDateString("Ausscheidedatum:", sb, null, newItem.getDroppingDate().toLocalDate());
        }
        if (!originalItem.getDroppingReason().equals(newItem.getDroppingReason())) {
            sb.append("Ausscheidegrund: {");
            sb.append(checkIfEmpty(originalItem.getDroppingReason()));
            sb.append("} -> {");
            sb.append(checkIfEmpty(newItem.getDroppingReason()));
            sb.append("}, ");
        }
        if (!originalItem.getComments().equals(newItem.getComments())) {
            sb.append("Anmerkungen: {");
            sb.append(checkIfEmpty(originalItem.getComments()));
            sb.append("} -> {");
            sb.append(checkIfEmpty(newItem.getComments()));
            sb.append("}, ");
        }
        if (pictureCounter > 0) {
            sb.append("Bilder / Dokumente hinzugefügt: {").append(pictureCounter).append("}  ");
        }

        if (!Objects.equals(originalItem.getDroppingQueue(), newItem.getDroppingQueue())) {
            if (DroppingQueueEnum.AUSSCHEIDEN.toString().equals(newItem.getDroppingQueue())) {
                sb.append("Ausscheidung angefordert: ");
                sb.append("Stückzahl: {");
                sb.append(newItem.getDroppingQueuePieces());
                sb.append("}, ");
                sb.append("Ausscheidegrund: {");
                sb.append(newItem.getDroppingQueueReason());
                sb.append("}, ");
                sb.append("Ausscheidedatum: {");
                sb.append(newItem.getDroppingQueueDate().toLocalDate());
                sb.append("}, ");
            } else if (DroppingQueueEnum.AUSSCHEIDEN.toString().equals(originalItem.getDroppingQueue()) &&
                    Objects.equals(originalItem.getPiecesDropped(), newItem.getPiecesDropped())) {
                sb.append("Ausscheidung abgelehnt.  ");
            } else if (DroppingQueueEnum.DEAKTIVIEREN.toString().equals(newItem.getDroppingQueue())) {
                sb.append("Deaktivierung angefordert: ");
                sb.append("Stückzahl: {");
                sb.append(newItem.getDroppingQueuePieces());
                sb.append("}, ");
                sb.append("Deaktivierungsgrund: {");
                sb.append(newItem.getDroppingQueueReason());
                sb.append("}, ");
                sb.append("Deaktivierungsdatum: {");
                sb.append(LocalDate.now());
                sb.append("}, ");
            } else if (DroppingQueueEnum.DEAKTIVIEREN.toString().equals(originalItem.getDroppingQueue())) {
                sb.append("Deaktivierung abgelehnt.  ");
            }
        }

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    private static String checkIfEmpty(String string) {
        if (string == null || string.isEmpty()) {
            return "leer";
        } else {
            return string;
        }
    }

    private static void getDateString(String firstPart, StringBuilder sb, LocalDate originalItemDate, LocalDate newItemDate) {
        sb.append(firstPart).append(" {");
        if (originalItemDate != null) {
            sb.append(originalItemDate);
        } else {
            sb.append("leer");
        }
        sb.append("} -> {");
        if (newItemDate != null) {
            sb.append(newItemDate);
        } else {
            sb.append("leer");
        }
        sb.append("}, ");
    }

}
