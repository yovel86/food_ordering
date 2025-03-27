package org.example.foodordering.activity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodordering.activity.PrepareFoodActivity;
import org.example.foodordering.util.Utilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrepareFoodActivityImpl implements PrepareFoodActivity {

    @Override
    public void prepareFood(Long orderId) {
        log.info("Preparing food for order: {}", orderId);
        Utilities.sleep(3000);
    }

}