package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.DeliverOrderActivity;
import org.example.foodordering.exception.OrderNotFoundException;
import org.example.foodordering.model.Order;
import org.example.foodordering.repository.OrderRepository;
import org.example.foodordering.util.Utilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliverOrderActivityImpl implements DeliverOrderActivity {

  private final OrderRepository orderRepository;

  @Override
  public void deliverOrder(Long orderId) {
    log.info("Checking if delivery is already started for Order: {}", orderId);

    Order order = this.orderRepository.findById(orderId).orElseThrow(
      () -> new OrderNotFoundException("Order not found: " + orderId)
    );

    if(order.getTrackingId() != null) {
      log.info("Delivery already started for order: {}, Tracking ID: {}", orderId, order.getTrackingId());
      return;
    }

    log.info("Starting Delivery for Order {}", orderId);
    String trackingId = Utilities.startDeliveryTracking(orderId);

    if (trackingId == null) {
      log.info("Delivery failed due to driver unavailability!");
      throw new RuntimeException("Delivery failed due to driver unavailability!");
    }

    order.setTrackingId(trackingId);
    this.orderRepository.save(order);

    Utilities.sleep(4000);
    log.info("Order {} delivered! Tracking ID: {}", orderId, trackingId);
  }

}
