package org.example.foodordering.controller;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowStub;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowClient workflowClient;

  @PostMapping("/stocksAreBack/{workflowId}")
  public ResponseEntity<String> stockSignal(@PathVariable String workflowId) {
    WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
    workflowStub.signal("stocksAreBack");
    return ResponseEntity.ok("Stocks are back, signal sent to workflow with ID: " + workflowId);
  }

  @PostMapping("/foodPrepared/{workflowId}")
  public ResponseEntity<String> foodPrepareSignal(@PathVariable String workflowId) {
    WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
    workflowStub.signal("foodPrepared");
    return ResponseEntity.ok("Food prepared, signal sent to workflow with ID: " + workflowId);
  }

}
