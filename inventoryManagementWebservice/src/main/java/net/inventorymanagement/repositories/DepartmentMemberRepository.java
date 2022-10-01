package net.inventorymanagement.repositories;

import net.inventorymanagement.model.DepartmentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentMemberRepository extends JpaRepository<DepartmentMember, Integer> {

    @Query(value = "SELECT * FROM department_member dmi WHERE dmi.user_id = :userid", nativeQuery = true)
    DepartmentMember findByUserId(Integer userid);

}
