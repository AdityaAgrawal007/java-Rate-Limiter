package com.cerberus.rateLimiter;

import com.cerberus.rateLimiter.algorithm.imMemoryFallback.FixedWindowAlgorithm;
import com.cerberus.rateLimiter.store.InMemoryStateStore;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark) // what's this annotation ?
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Threads(50)
public class RateLimiterBenchmark {
    private InMemoryStateStore store;

    @Setup
    public void setup() {
        store = new InMemoryStateStore(new FixedWindowAlgorithm(Integer.MAX_VALUE, Duration.ofSeconds(60)));
    }

    @Benchmark
    public void benchmarkTryConsume() {
        store.tryConsume("client01");
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(RateLimiterBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
