package org.example.foodordering.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ProcessPaymentActivity {

    @ActivityMethod
    void processPayment(Long orderId);

}
