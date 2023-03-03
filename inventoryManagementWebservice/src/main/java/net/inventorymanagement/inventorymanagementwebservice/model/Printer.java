package net.inventorymanagement.inventorymanagementwebservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "printer")
@Getter
@Setter
@ToString
public class Printer implements Comparable<Printer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String printerName;
    private String printerModel;
    private String printerIp;
    private String labelFormat;

    @Override
    public int compareTo(Printer o) {
        return this.getPrinterName().compareToIgnoreCase(o.getPrinterName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Printer printer = (Printer) o;
        return printerName.equals(printer.printerName) && printerIp.equals(printer.printerIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(printerName, printerIp);
    }

}
