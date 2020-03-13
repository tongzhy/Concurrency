package com.tongzy.concurrency.example.atomic;

import com.tongzy.concurrency.annotations.ThreadSafe;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

@Slf4j
@ThreadSafe
public class TestAtomicStampedReference implements Cloneable {

    @Getter
    @Setter
    private volatile int count;

    public TestAtomicStampedReference(int count) {
        this.count = count;
    }

    public static void main(String[] args) {
        TestAtomicStampedReference reference = new TestAtomicStampedReference(0);
        AtomicStampedReference<TestAtomicStampedReference> countStampedReference =
            new AtomicStampedReference<>(reference, 0);
        TestAtomicStampedReference newReference = new TestAtomicStampedReference(100);
        if (countStampedReference.compareAndSet(reference, newReference, 0, 1)) {
            log.info("update success, count:{}", countStampedReference.getReference().getCount());
        }
    }
}