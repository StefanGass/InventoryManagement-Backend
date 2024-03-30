package net.inventorymanagement.usermanagementwebservice.repository;

import net.inventorymanagement.usermanagementwebservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query(value = "SELECT * FROM team t WHERE t.id = :id", nativeQuery = true)
    Team findByTeamId(int id);

}
