package org.example.foodordering.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface CheckStockActivity {

  @ActivityMethod
  boolean checkStock(List<Long> itemIds);

}
