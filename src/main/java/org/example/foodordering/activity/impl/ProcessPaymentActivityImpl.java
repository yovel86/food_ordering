package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.ProcessPaymentActivity;
import org.example.foodordering.exception.OrderNotFoundException;
import org.example.foodordering.model.Order;
import org.example.foodordering.model.OrderStatus;
import org.example.foodordering.repository.OrderRepository;
import org.example.foodordering.util.Utilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentActivityImpl implements ProcessPaymentActivity {

    private final OrderRepository orderRepository;

    @Override
    public void processPayment(Long orderId) {
        log.info("Checking if payment is already processed for order: {}", orderId);
        Order order = this.orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found with this ID " + orderId)
        );
        if (order.isPaymentDone()) {
            log.info("Payment already processed for order: {}", orderId);
            return;
        }

        log.info("Processing payment for Order: {}", orderId);
        boolean paymentSuccess = Utilities.fakePaymentGateway(orderId);

        if (!paymentSuccess) {
            log.info("Payment failed for Order: {}", orderId);
            throw new RuntimeException("Payment failed for Order: " + orderId);
        }

        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setPaymentDone(true);
        this.orderRepository.save(order);

        log.info("Payment successful for order: {}", orderId);
    }

}