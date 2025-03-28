package org.example.foodordering.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.CheckStockActivity;
import org.example.foodordering.activity.*;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FoodOrderWorkflowImpl implements FoodOrderWorkflow {

  private static final Logger logger = Workflow.getLogger(FoodOrderWorkflowImpl.class);

  private String requestId;
  private boolean stocksAreBack = false;
  private boolean foodPrepared = false;

  @Override
  public String processOrder(String requestId, List<Long> itemIds) {
    if(itemIds == null || itemIds.isEmpty()) {
      throw ApplicationFailure.newNonRetryableFailure("Item IDs cannot be null or empty", "INVALID_INPUT");
    }

    if(this.requestId == null) {
      this.requestId = requestId;
    }

    ActivityOptions activityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(10))
        .setRetryOptions(RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setMaximumInterval(Duration.ofSeconds(30))
                .setMaximumAttempts(5)
                .setBackoffCoefficient(2.0)
                .build())
        .build();

    CheckStockActivity checkStockActivity = Workflow.newActivityStub(CheckStockActivity.class, activityOptions);
    CreateOrderActivity createOrderActivity = Workflow.newActivityStub(CreateOrderActivity.class, activityOptions);
    ProcessPaymentActivity processPaymentActivity = Workflow.newActivityStub(ProcessPaymentActivity.class, activityOptions);
    PrepareFoodActivity prepareFoodActivity = Workflow.newActivityStub(PrepareFoodActivity.class, activityOptions);
    DeliverOrderActivity deliverOrderActivity = Workflow.newActivityStub(DeliverOrderActivity.class, activityOptions);
    CompleteOrderActivity completeOrderActivity = Workflow.newActivityStub(CompleteOrderActivity.class, activityOptions);

    logger.info("Starting workflow with requestId: {}, items: {}", requestId, itemIds);

    boolean stockAvailable = checkStockActivity.checkStock(itemIds);
    if(!stockAvailable) {
      logger.info("Out of stock! Waiting for stock update...");
      boolean signalReceived = Workflow.await(Duration.ofMinutes(1), () -> stocksAreBack);
      if(!signalReceived) {
        logger.warn("Stock update timeout! Cancelling the order...");
        throw ApplicationFailure.newFailure("Out of stock!", "STOCK_ERROR");
      }
      logger.info("Stock updated! Proceeding with order...");
    }

    Long orderId = createOrderActivity.createOrder(itemIds, requestId);
    processPaymentActivity.processPayment(orderId);

    prepareFoodActivity.prepareFood(orderId);
    boolean isFoodPrepared = Workflow.await(Duration.ofMinutes(1), () -> foodPrepared);
    if(!isFoodPrepared) {
      logger.warn("Food preparation timeout!");
      throw ApplicationFailure.newFailure("Food is not prepared in time!", "FOOD_PREPARATION_ERROR");
    }
    logger.info("Food prepared for order: {}", orderId);

    deliverOrderActivity.deliverOrder(orderId);
    completeOrderActivity.completeOrder(orderId);

    logger.info("Workflow completed for Order ID: {}", orderId);

    return "Order with ID: " + orderId + " is COMPLETED";
  }

  @Override
  public void stocksAreBack() {
    stocksAreBack = true;
  }

  @Override
  public void foodPrepared() {
    foodPrepared = true;
  }

}
