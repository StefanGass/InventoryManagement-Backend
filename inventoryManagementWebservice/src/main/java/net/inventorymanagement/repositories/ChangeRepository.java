package net.inventorymanagement.repositories;

import net.inventorymanagement.model.Change;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeRepository extends JpaRepository<Change, Integer> {
}
