package com.tongzy.concurrency.example.stack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class StackWithStamp {

    private final AtomicStampedReference<Node> headReference;

    private AtomicInteger stamp;

    public StackWithStamp() {
        Node node = new Node(0);
        stamp = new AtomicInteger(Integer.MIN_VALUE);
        headReference = new AtomicStampedReference<>(node, stamp.getAndIncrement());
    }

    public int pop() {
        Node top = popNode();
        return top == null ? Integer.MAX_VALUE : top.value;
    }

    public void push(int value) {
        Node node = new Node(value);
        Node expectedHead;
        int expectedStamp;
        Node newHead;
        do {
            expectedHead = headReference.getReference();
            expectedStamp = headReference.getStamp();
            node.next = expectedHead.next;
            node.min = expectedHead.next == null ? value : Math.min(value, expectedHead.next.min);
            newHead = new Node(0);
            newHead.next = node;
            //CAS
        } while (!headReference.compareAndSet(expectedHead, newHead, expectedStamp, stamp.getAndIncrement()));
    }

    public int min() {
        return headReference.getReference().next.min;
    }

    private Node peek() {
        return headReference.getReference().next;
    }

    private Node popNode() {
        Node expectedHead;
        int expectedStamp;
        Node newHead;
        do {
            expectedHead = headReference.getReference();
            if (expectedHead.next == null) {
                return null;
            }
            expectedStamp = headReference.getStamp();
            newHead = new Node(0);
            newHead.next = expectedHead.next.next;
            //CAS
        } while (!headReference.compareAndSet(expectedHead, newHead, expectedStamp, stamp.getAndIncrement()));

        return expectedHead.next;
    }

    class Node {
        int value;
        int min;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }
}
