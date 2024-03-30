package net.inventorymanagement.usermanagementwebservice.repository;

import net.inventorymanagement.usermanagementwebservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM user u WHERE u.id = :id", nativeQuery = true)
    User findByUserId(int id);

    @Query(value = "SELECT * FROM user u WHERE u.user_logon_name = :userLogonName", nativeQuery = true)
    User findByUserLogonName(String userLogonName);

}
