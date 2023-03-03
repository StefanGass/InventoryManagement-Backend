package net.inventorymanagement.inventorymanagementwebservice.repositories;

import net.inventorymanagement.inventorymanagementwebservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value = "SELECT * FROM category c WHERE c.category_name = :name", nativeQuery = true)
    Category findByCategoryName(String name);

    @Query(value = "SELECT * FROM category c WHERE c.prefix = :prefix", nativeQuery = true)
    Category findByCategoryPrefix(String prefix);

}
