package org.example.foodordering.repository;

import org.example.foodordering.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

  boolean existsByIdAndIsAvailableTrue(Long itemId);

}
