package com.cerberus.rateLimiter.core;

import com.cerberus.rateLimiter.extractor.IpKeyExtractor;
import com.cerberus.rateLimiter.store.InMemoryStateStore;
import jakarta.servlet.http.HttpServletRequest;


import java.time.Duration;

public class RateLimiterImpl implements RateLimiter{
    private long tokenLimit;
    private Duration timeWindow;
    private StateStore store;

    public RateLimiterImpl(long tokenLimit, Duration timeWindow, StateStore store) {
        this.tokenLimit = tokenLimit;
        this.timeWindow = timeWindow;
        this.store = store;
    }

    public RateLimitResult tryAquire(String clientId) {
        return store.tryConsume(clientId, tokenLimit, timeWindow);
    }
}