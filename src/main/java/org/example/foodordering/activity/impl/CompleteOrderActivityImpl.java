package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.CompleteOrderActivity;
import org.example.foodordering.exception.OrderNotFoundException;
import org.example.foodordering.model.Order;
import org.example.foodordering.model.OrderStatus;
import org.example.foodordering.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompleteOrderActivityImpl implements CompleteOrderActivity {

  private final OrderRepository orderRepository;

  @Override
  public void completeOrder(Long orderId) {
    log.info("Completing order: {}", orderId);

    Order order = this.orderRepository.findById(orderId).orElseThrow(
      () -> new OrderNotFoundException("Order not found with this ID " + orderId)
    );
    order.setOrderStatus(OrderStatus.COMPLETED);
    this.orderRepository.save(order);

    log.info("Order {} completed!", orderId);
  }

}
