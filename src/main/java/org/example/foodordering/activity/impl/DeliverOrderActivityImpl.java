package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.DeliverOrderActivity;
import org.example.foodordering.util.Utilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliverOrderActivityImpl implements DeliverOrderActivity {

  @Override
  public void deliverOrder(Long orderId) {
    log.info("Delivering order: {}", orderId);
    String trackingId = Utilities.startDeliveryTracking(orderId);

    if (trackingId == null) {
      log.info("Delivery failed due to driver unavailability!");
      throw new RuntimeException("Delivery failed due to driver unavailability!");
    }

    log.info("Delivery started for Order ID: {}, Tracking ID: {}", orderId, trackingId);
    Utilities.sleep(4000);
    log.info("Order {} delivered! Tracking ID: {}", orderId, trackingId);
  }

}
