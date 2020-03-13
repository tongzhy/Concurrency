package com.tongzy.concurrency.example.atomic;

import com.tongzy.concurrency.annotations.ThreadSafe;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@Slf4j
@ThreadSafe
public class TestAtomicFieldUpdater {

    private static final AtomicIntegerFieldUpdater<TestAtomicFieldUpdater> COUNT_UPDATER =
        AtomicIntegerFieldUpdater.newUpdater(TestAtomicFieldUpdater.class, "count");

    /**
     * must be volatile and non static field
     */
    @Getter
    private volatile int count = 100;

    public static void main(String[] args) {
        TestAtomicFieldUpdater fieldUpdater = new TestAtomicFieldUpdater();
        if (COUNT_UPDATER.compareAndSet(fieldUpdater, 100, 200)) {
            log.info("update success 1, count:{}", fieldUpdater.getCount());
        } else {
            log.info("update failed");
        }
        if (COUNT_UPDATER.compareAndSet(fieldUpdater, 100, 200)) {
            log.info("update success 2, count:{}", fieldUpdater.getCount());
        } else {
            log.info("update failed");
        }
    }
}