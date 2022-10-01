package net.inventorymanagement.repositories;

import net.inventorymanagement.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    @Query(value = "SELECT * FROM supplier s WHERE s.supplier_name = :name", nativeQuery = true)
    Supplier findBySupplierName(String name);

}
