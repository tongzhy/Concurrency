package com.tongzy.concurrency;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@BenchmarkMode(Mode.Throughput)//基准测试类型
@OutputTimeUnit(TimeUnit.MICROSECONDS)//基准测试结果的时间类型
@Warmup(iterations = 2)//预热的迭代次数
@Threads(8)//测试线程数量
public class AtomicBenchmark {

    private static AtomicLong count = new AtomicLong();
    private static LongAdder longAdder = new LongAdder();

    @Test
    void test() throws Exception {
        Options options = new OptionsBuilder()
            .include(AtomicBenchmark.class.getName())
            .forks(1)
            .build();
        Collection<RunResult> run = new Runner(options).run();
        Assert.noNullElements(run, "[Assertion failed] - all element of this collection must not null");
    }

    @Benchmark
    public void run0() {
        count.getAndIncrement();
    }

    @Benchmark
    public void run1() {
        longAdder.increment();
    }
}