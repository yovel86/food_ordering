package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.CreateOrderActivity;
import org.example.foodordering.model.Order;
import org.example.foodordering.model.OrderStatus;
import org.example.foodordering.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderActivityImpl implements CreateOrderActivity {

  private final OrderRepository orderRepository;

  @Override
  public Long createOrder(List<Long> itemIds, String requestId) {
    log.info("Checking if Order exists for requestId: {}", requestId);
    Optional<Order> existingOrder = this.orderRepository.findByRequestId(requestId);
    if(existingOrder.isPresent()) {
      log.info("Order already exists with ID: {}", existingOrder.get().getId());
      return existingOrder.get().getId();
    }

    log.info("Creating new Order...");
    Order order = new Order();
    order.setOrderStatus(OrderStatus.PENDING);
    order.setItemIds(itemIds);
    order.setRequestId(requestId);

    order = this.orderRepository.save(order);
    log.info("Order {} created successfully!", order.getId());

    return order.getId();
  }

}