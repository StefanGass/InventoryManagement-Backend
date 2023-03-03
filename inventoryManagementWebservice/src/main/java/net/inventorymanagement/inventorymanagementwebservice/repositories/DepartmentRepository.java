package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query(value = "SELECT * FROM department d WHERE d.id = :id", nativeQuery = true)
    Department findByDepartmentId(Integer id);

    @Query(value = "SELECT * FROM department d WHERE d.department_name = :name", nativeQuery = true)
    Department findByDepartmentName(String name);

}
