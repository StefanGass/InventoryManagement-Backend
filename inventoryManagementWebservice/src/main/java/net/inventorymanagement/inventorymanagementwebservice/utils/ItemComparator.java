package net.inventorymanagement.inventorymanagementwebservice.utils;

import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;

import java.time.LocalDate;

public class ItemComparator {

    public static String getChangeString(InventoryItem originalItem, InventoryItem newItem, int pictureCounter) {
        StringBuilder sb = new StringBuilder();
        if (!originalItem.getItemName().equals(newItem.getItemName())) {
            sb.append("Beschreibung: {");
            sb.append(checkIfEmpty(originalItem.getItemName()));
            sb.append("} -> {");
            sb.append(checkIfEmpty(newItem.getItemName()));
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
            if (sb.length() > 0) {
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
                getDateString("Ausscheidedatum:", sb, originalItem.getIssueDate().toLocalDate(), newItem.getIssueDate().toLocalDate());
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
        if (!originalItem.getOldItemNumber().equals(newItem.getOldItemNumber())) {
            if (sb.length() > 0) {
                sb.append("alte Inventarnummer: {");
            } else {
                sb.append("Alte Inventarnummer {");
            }
            sb.append(originalItem.getOldItemNumber());
            sb.append("} -> {");
            sb.append(newItem.getOldItemNumber());
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
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    private static String checkIfEmpty(String string) {
        if (string == null || string.equals("")) {
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
