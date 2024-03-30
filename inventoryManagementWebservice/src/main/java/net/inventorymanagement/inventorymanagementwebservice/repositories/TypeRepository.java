package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {

    @Query(value = "SELECT * FROM type t WHERE t.id = :id", nativeQuery = true)
    Type findByTypeId(int id);

    @Query(value = "SELECT * FROM type t WHERE t.type_name = :typeName", nativeQuery = true)
    Type findByTypeName(String typeName);

}
