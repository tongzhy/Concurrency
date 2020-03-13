package com.tongzy.concurrency.example.stack;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class ConcurrentStack {

    // setup to use Unsafe.compareAndSwapObject for updates
    private static final Unsafe unsafe;
    private static final long firstOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            firstOffset = unsafe.objectFieldOffset(ConcurrentStack.class.getDeclaredField("first"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    @sun.misc.Contended
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
        } while (!unsafe.compareAndSwapObject(this, firstOffset, expect, update));
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
        } while (!unsafe.compareAndSwapObject(this, firstOffset, expect, update));
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
