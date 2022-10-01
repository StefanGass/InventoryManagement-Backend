package net.inventorymanagement.repositories;

import net.inventorymanagement.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Integer> {
}
