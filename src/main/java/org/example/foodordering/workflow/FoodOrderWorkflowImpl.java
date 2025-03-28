package org.example.foodordering.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.CompletablePromise;
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
  private boolean canProceed = false;
  private boolean foodPrepared = false;

  @Override
  public String processOrder(List<Long> itemIds) {
    if(itemIds == null || itemIds.isEmpty()) {
      throw ApplicationFailure.newNonRetryableFailure("Item IDs cannot be null or empty", "INVALID_INPUT");
    }

    if(requestId == null) {
      requestId = Workflow.randomUUID().toString();
      logger.info("Generated new requestId: {}", requestId);
    } else {
      logger.info("Resuming workflow with existing requestId: {}", requestId);
    }

    ActivityOptions activityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(10))
        .build();

    CheckStockActivity checkStockActivity = Workflow.newActivityStub(
        CheckStockActivity.class,
        ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofMinutes(2))
          .build()
    );

    CreateOrderActivity createOrderActivity = Workflow.newActivityStub(CreateOrderActivity.class, activityOptions);
    ProcessPaymentActivity processPaymentActivity = Workflow.newActivityStub(ProcessPaymentActivity.class, activityOptions);
    PrepareFoodActivity prepareFoodActivity = Workflow.newActivityStub(PrepareFoodActivity.class, activityOptions);
    DeliverOrderActivity deliverOrderActivity = Workflow.newActivityStub(DeliverOrderActivity.class, activityOptions);
    CompleteOrderActivity completeOrderActivity = Workflow.newActivityStub(CompleteOrderActivity.class, activityOptions);

    logger.info("Starting workflow with requestId: {}, items: {}", requestId, itemIds);

    boolean stockAvailable = checkStockActivity.checkStock(itemIds);
    if(!stockAvailable) {
      logger.info("Out of stock! Waiting for stock update...");
      boolean signalReceived = Workflow.await(Duration.ofMinutes(1), () -> canProceed);
      if(!signalReceived) {
        logger.warn("Stock update timeout! Cancelling the order...");
        throw ApplicationFailure.newNonRetryableFailure("Out of stock!", "STOCK_ERROR");
      }
      logger.info("Stock updated! Proceeding with order...");
    }

    Long orderId = createOrderActivity.createOrder(itemIds, requestId);
    processPaymentActivity.processPayment(orderId);

    prepareFoodActivity.prepareFood(orderId);
    Workflow.await(() -> foodPrepared);
    logger.info("Food prepared for order: {}", orderId);

    deliverOrderActivity.deliverOrder(orderId);
    completeOrderActivity.completeOrder(orderId);

    logger.info("Workflow completed for Order ID: {}", orderId);

    return "Order with ID: " + orderId + " is COMPLETED";
  }

  @Override
  public void stocksAreBack() {
    canProceed = true;
  }

  @Override
  public void foodPrepared() {
    foodPrepared = true;
  }

}
