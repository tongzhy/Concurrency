package com.tongzy.concurrency.example.atomic;

import com.tongzy.concurrency.annotations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@ThreadSafe
public class ConcurrentWithAtomicBoolean {

    private static final int CLIENT_TOTAL = 5000;

    private static final int THREAD_TOTAL = 200;

    private static AtomicBoolean isHappened = new AtomicBoolean(false);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(THREAD_TOTAL);
        final CountDownLatch countDownLatch = new CountDownLatch(CLIENT_TOTAL);
        for (int i = 0; i < CLIENT_TOTAL; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    execute();
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
        log.info("isHappened:{}", isHappened.get());
        executorService.shutdown();
    }

    /**
     * only execute once!
     */
    private static void execute() {
        if (isHappened.compareAndSet(false, true)) {
            log.info("execute:{}", isHappened.get());
        }
    }
}