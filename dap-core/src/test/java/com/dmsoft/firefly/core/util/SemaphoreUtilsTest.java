package com.dmsoft.firefly.core.util;

import com.dmsoft.firefly.sdk.utils.SemaphoreUtils;

public class SemaphoreUtilsTest {
    public static void main(String[] args) {
        SemaphoreUtils.lockSemaphore("Lock");
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("AASDFB");
            SemaphoreUtils.releaseSemaphore("Lock");
        });
        thread.run();
        SemaphoreUtils.lockSemaphore("Lock");
        SemaphoreUtils.releaseSemaphore("Lock");
        System.out.println("ASDFAGC");
    }
}
