package com.cerberus.rateLimiter.core;

public interface RateLimiter {
    RateLimitResult tryAquire(String clientId);
}
