package org.example.foodordering.util;

import java.util.Random;
import java.util.UUID;

public class Utilities {

    static final Random random = new Random();

    public static boolean fakePaymentGateway(Long orderId) {
        return random.nextInt(10) < 6; // 60% success rate
    }

    public static String startDeliveryTracking(Long orderId) {
        if (random.nextInt(10) < 6) {  // 60% success rate
            return "TRACK-" + UUID.randomUUID();
        }
        return null;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
