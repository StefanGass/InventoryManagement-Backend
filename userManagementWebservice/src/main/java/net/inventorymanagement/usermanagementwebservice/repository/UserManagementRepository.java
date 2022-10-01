package net.inventorymanagement.usermanagementwebservice.repository;

import net.inventorymanagement.usermanagementwebservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User management repository.
 */

@Repository
public interface UserManagementRepository extends JpaRepository<User, Integer> {
}
