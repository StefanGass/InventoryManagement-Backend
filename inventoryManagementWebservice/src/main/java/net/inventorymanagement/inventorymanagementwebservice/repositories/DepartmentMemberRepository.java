package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.DepartmentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentMemberRepository extends JpaRepository<DepartmentMember, Integer> {

    @Query(value = "SELECT * FROM department_member d WHERE d.user_id = :userId", nativeQuery = true)
    DepartmentMember findByUserId(int userId);

}
