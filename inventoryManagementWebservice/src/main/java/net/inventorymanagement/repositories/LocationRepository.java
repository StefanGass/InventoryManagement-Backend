package net.inventorymanagement.repositories;

import net.inventorymanagement.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query(value = "SELECT * FROM location l WHERE l.location_name = :name", nativeQuery = true)
    Location findByLocationName(String name);

}
