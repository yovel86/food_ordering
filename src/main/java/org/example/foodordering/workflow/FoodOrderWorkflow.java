package org.example.foodordering.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface FoodOrderWorkflow {

    @WorkflowMethod
    String processOrder(String requestId, List<Long> itemIds);

    @SignalMethod
    void stocksAreBack();

    @SignalMethod
    void foodPrepared();

}