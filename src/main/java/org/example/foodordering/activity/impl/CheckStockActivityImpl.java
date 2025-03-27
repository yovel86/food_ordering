package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.CheckStockActivity;
import org.example.foodordering.repository.FoodItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckStockActivityImpl implements CheckStockActivity {

  private final FoodItemRepository foodItemRepository;

  @Override
  public boolean checkStock(List<Long> itemIds) {
    log.info("Checking the stock for item IDs: {}", itemIds);
    List<Long> unavailableItems = itemIds.stream()
            .filter(itemId -> !foodItemRepository.existsByIdAndIsAvailableTrue(itemId))
            .toList();
    return unavailableItems.isEmpty();
  }

}