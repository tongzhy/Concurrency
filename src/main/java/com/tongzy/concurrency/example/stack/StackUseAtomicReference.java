package com.tongzy.concurrency.example.stack;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class StackUseAtomicReference {

    private static final AtomicReferenceFieldUpdater<StackUseAtomicReference, Node> reference =
        AtomicReferenceFieldUpdater.newUpdater(StackUseAtomicReference.class, Node.class, "first");

    private volatile Node first = null;

    public Integer pop() {
        Node top = popNode();
        return top == null ? null : top.value;
    }

    public void push(int value) {
        Node expect;
        Node update = new Node(value);
        do {
            expect = first;
            update.next = expect;
            update.min = expect == null ? value : Math.min(value, expect.min);
        } while (!reference.compareAndSet(this, expect, update));
    }

    public Integer min() {
        Node top = first;
        return top == null ? null : top.min;
    }

    public Integer peek() {
        Node top = first;
        return top == null ? null : top.value;
    }

    private Node popNode() {
        Node expect, update;
        do {
            expect = first;
            if (expect == null)
                return null;
            update = expect.next;
        } while (!reference.compareAndSet(this, expect, update));
        return expect;
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
