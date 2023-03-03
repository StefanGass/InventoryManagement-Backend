package net.inventorymanagement.usermanagementwebservice.repository;

import net.inventorymanagement.usermanagementwebservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User management repository.
 */

@Repository
public interface UserManagementRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM user u WHERE u.id = :id", nativeQuery = true)
    User findByUserId(Integer id);

    @Query(value = "SELECT * FROM user u WHERE u.group_id = :groupId", nativeQuery = true)
    List<User> findByGroupId(Integer groupId);

}
