package com.tongzy.concurrency.example.stack;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.*;

@BenchmarkMode(Mode.Throughput)//基准测试类型
@OutputTimeUnit(TimeUnit.MICROSECONDS)//基准测试结果的时间类型
@Warmup(iterations = 1)//预热的迭代次数
@Threads(8)//测试线程数量
public class StackTest {

    private static final StackWithStamp STACK0 = new StackWithStamp();
    private static final ConcurrentStack STACK1 = new ConcurrentStack();
    private static final StackUseAtomicReference STACK2 = new StackUseAtomicReference();
    private static final Deque<Integer> COMPARE_STACK = new ConcurrentLinkedDeque<>();

    private static final Random random = new Random();

    @Test
    void benchmark() throws Exception {
        Options options = new OptionsBuilder()
            .include(StackTest.class.getName())
            .forks(1)
            .build();
        Collection<RunResult> run = new Runner(options).run();
        Assert.noNullElements(run, "[Assertion failed] - all element of this collection must not null");
    }

    @Benchmark
    public void run0() {
        STACK0.push(random.nextInt());
        STACK0.pop();
    }

    @Benchmark
    public void run1() {
        STACK1.push(random.nextInt());
        STACK1.pop();
    }

    @Benchmark
    public void run2() {
        STACK2.push(random.nextInt());
        STACK2.pop();
    }

    @Benchmark
    public void compare() {
        COMPARE_STACK.push(random.nextInt());
        COMPARE_STACK.pop();
    }


    @Test
    void verify() {
        int threadTotal = 100;
        int maxConcurrency = 8;
        int numberCount = 100;

        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(maxConcurrency);
        final CountDownLatch countDownLatch = new CountDownLatch(threadTotal);
        for (int i = 0; i < threadTotal; i++) {
            executorService.execute(() -> {
                Random r = new Random();
                try {
                    semaphore.acquire();
                    for (int j = 0; j < numberCount; j++)
                        STACK2.push(r.nextInt(10000));
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        int[] array = new int[threadTotal * numberCount];
        for (int i = 0; i < threadTotal * numberCount; i++) {
            int min = STACK2.min();
            System.out.println(min + "\t\t" + STACK2.pop());
            array[i] = min;
        }
        //验证是否：单调不减数列
        boolean isUnabated = true;
        for (int i = 1; i < threadTotal * numberCount; i++) {
            if (array[i - 1] > array[i]) {
                isUnabated = false;
                break;
            }
        }
        Assert.isTrue(isUnabated, "[Assertion failed] - Stack is not thread safe");
    }
}