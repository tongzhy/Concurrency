package com.tongzy.concurrency.example.atomic;

import com.tongzy.concurrency.annotations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@ThreadSafe
public class ConcurrentWithLongAdder {

    private static final int CLIENT_TOTAL = 5000;

    private static final int THREAD_TOTAL = 200;

    private static LongAdder count = new LongAdder();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(THREAD_TOTAL);
        final CountDownLatch countDownLatch = new CountDownLatch(CLIENT_TOTAL);
        for (int i = 0; i < CLIENT_TOTAL; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        log.info("count:{}", count);
        executorService.shutdown();
    }

    private static void add() {
        count.increment();
    }
}