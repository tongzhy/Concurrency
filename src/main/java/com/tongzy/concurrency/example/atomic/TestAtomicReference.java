package com.tongzy.concurrency.example.atomic;

import com.tongzy.concurrency.annotations.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ThreadSafe
public class TestAtomicReference {

    private static AtomicReference<Integer> count = new AtomicReference<>(0);

    public static void main(String[] args) {
        count.compareAndSet(0, 2);
        count.compareAndSet(1, 3);
        count.compareAndSet(2, 4);
        count.compareAndSet(3, 5);
        log.info("count:{}", count.get());
    }
}