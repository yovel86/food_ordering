package org.example.foodordering.controller;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.example.foodordering.dto.OrderRequestDto;
import org.example.foodordering.workflow.FoodOrderWorkflow;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FoodOrderController {

  private final WorkflowClient workflowClient;

  @PostMapping("/process-order")
  public ResponseEntity<String> processOrder(@RequestBody OrderRequestDto orderRequestDto) {
    FoodOrderWorkflow foodOrderWorkflow = workflowClient.newWorkflowStub(
      FoodOrderWorkflow.class,
      WorkflowOptions.newBuilder()
          .setWorkflowId("food-order-" + UUID.randomUUID())
          .setTaskQueue("FOOD_ORDER_TASK_QUEUE")
          .build()
    );

    WorkflowExecution execution = WorkflowClient.start(
      foodOrderWorkflow::processOrder,
      orderRequestDto.getItemIds()
    );

    return ResponseEntity.ok("Order workflow started with ID: " + execution.getWorkflowId());
  }

}
