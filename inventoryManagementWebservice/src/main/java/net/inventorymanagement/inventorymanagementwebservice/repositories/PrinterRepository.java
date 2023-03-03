package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.Printer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, Integer> {

    @Query(value = "SELECT * FROM printer p WHERE p.id = :id", nativeQuery = true)
    Printer findByPrinterId(Integer id);

    @Query(value = "SELECT * FROM printer p WHERE p.printer_name = :printerName", nativeQuery = true)
    Printer findByPrinterName(String printerName);

    @Query(value = "SELECT * FROM printer p WHERE p.printer_ip = :printerIp", nativeQuery = true)
    Printer findByPrinterIp(String printerIp);

}
