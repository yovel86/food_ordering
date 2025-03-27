package org.example.foodordering.worker;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import org.example.foodordering.activity.*;
import org.example.foodordering.workflow.FoodOrderWorkflowImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FoodOrderWorker {

  private final WorkflowClient workflowClient;

  private final CheckStockActivity checkStockActivity;
  private final CreateOrderActivity createOrderActivity;
  private final ProcessPaymentActivity processPaymentActivity;
  private final PrepareFoodActivity prepareFoodActivity;
  private final DeliverOrderActivity deliverOrderActivity;
  private final CompleteOrderActivity completeOrderActivity;

  @Bean
  public WorkerFactory workerFactory() {
    WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
    Worker worker = factory.newWorker("FOOD_ORDER_TASK_QUEUE");

    worker.registerWorkflowImplementationTypes(FoodOrderWorkflowImpl.class);
    worker.registerActivitiesImplementations(
      checkStockActivity,
      createOrderActivity,
      processPaymentActivity,
      prepareFoodActivity,
      deliverOrderActivity,
      completeOrderActivity
    );

    factory.start();

    return factory;
  }

}