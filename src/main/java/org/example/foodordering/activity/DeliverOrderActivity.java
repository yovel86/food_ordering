package org.example.foodordering.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DeliverOrderActivity {

  @ActivityMethod
  void deliverOrder(Long orderId);

}
