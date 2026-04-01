package com.cerberus.rateLimiter.core;
import java.time.Duration;

public record RateLimitResult(boolean accepted, long resetTimestamp, long remainingTokens) {
}
