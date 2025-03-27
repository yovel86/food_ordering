package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodorderingsample.activity.CreateOrderActivity;
import org.example.foodorderingsample.model.Order;
import org.example.foodorderingsample.model.OrderStatus;
import org.example.foodorderingsample.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderActivityImpl implements CreateOrderActivity {

  private final OrderRepository orderRepository;

  @Override
  public Long createOrder(List<Long> itemIds) {
    log.info("Creating new order for items: {}", itemIds);

    Order order = new Order();
    order.setOrderStatus(OrderStatus.PENDING);
    order.setItemIds(itemIds);

    order = this.orderRepository.save(order);
    log.info("Order {} created successfully!", order.getId());

    return order.getId();
  }

}