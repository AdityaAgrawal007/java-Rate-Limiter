package com.cerberus.rateLimiter;

import com.cerberus.rateLimiter.algorithm.imMemoryFallback.FixedWindowAlgorithm;
import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.store.InMemoryStateStore;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CorrectnessTest {

    // Test 1: Exactly N allowed, N+1 rejected
    @Test
    void exactlyNRequestsAllowedThenRejected() {
        int LIMIT = 5;
        InMemoryStateStore store = new InMemoryStateStore(new FixedWindowAlgorithm(LIMIT, Duration.ofSeconds(60)));

        for (int i = 0; i < LIMIT; i++) {
            assertTrue(store.tryConsume("client01").accepted(), "Request " + (i + 1) + " should be accepted");
        }

        assertFalse(store.tryConsume("client01").accepted(), "Request N+1 should be rejected");
    }

    // Test 2: Window resets correctly
    @Test
    void windowResetsAfterExpiry() throws InterruptedException {
        int LIMIT = 3;
        InMemoryStateStore store = new InMemoryStateStore(new FixedWindowAlgorithm(LIMIT, Duration.ofSeconds(1)));

        // exhaust the quota
        for (int i = 0; i < LIMIT; i++) {
            store.tryConsume("client01");
        }
        assertFalse(store.tryConsume("client01").accepted(), "Should be rejected before reset");

        // wait for window to expire
        Thread.sleep(1100);

        assertTrue(store.tryConsume("client01").accepted(), "Should be accepted after window reset");
    }

    // Test 3: Two clients have independent counters
    @Test
    void twoClientsAreIndependent() {
        int LIMIT = 3;
        InMemoryStateStore store = new InMemoryStateStore(new FixedWindowAlgorithm(LIMIT, Duration.ofSeconds(60)));

        // exhaust client01
        for (int i = 0; i < LIMIT; i++) {
            store.tryConsume("client01");
        }
        assertFalse(store.tryConsume("client01").accepted(), "client01 should be rejected");

        // client02 should be unaffected
        assertTrue(store.tryConsume("client02").accepted(), "client02 should still be accepted");
    }

    // Test 4: Null/empty key doesn't crash
    @Test
    void nullKeyDoesNotCrash() {
        InMemoryStateStore store = new InMemoryStateStore(new FixedWindowAlgorithm(5, Duration.ofSeconds(60)));
        assertDoesNotThrow(() -> store.tryConsume(null));
    }

    @Test
    void emptyKeyDoesNotCrash() {
        InMemoryStateStore store = new InMemoryStateStore(new FixedWindowAlgorithm(5, Duration.ofSeconds(60)));
        assertDoesNotThrow(() -> store.tryConsume(""));
    }

    public static void main(String[] args) throws InterruptedException {
        CorrectnessTest test = new CorrectnessTest();

        test.exactlyNRequestsAllowedThenRejected();
        System.out.println("Test 1 passed: Exactly N allowed, N+1 rejected");

        test.windowResetsAfterExpiry();
        System.out.println("Test 2 passed: Window resets correctly");

        test.twoClientsAreIndependent();
        System.out.println("Test 3 passed: Two clients are independent");

        test.nullKeyDoesNotCrash();
        System.out.println("Test 4 passed: Null key does not crash");

        test.emptyKeyDoesNotCrash();
        System.out.println("Test 5 passed: Empty key does not crash");

        System.out.println("\nAll correctness tests passed.");
    }
}