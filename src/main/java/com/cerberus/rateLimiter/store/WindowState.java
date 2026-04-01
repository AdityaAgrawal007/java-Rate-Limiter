// all feilds in records are by default final i.e. immutable
package com.cerberus.rateLimiter.store;

public record WindowState(long startTimestamp, long count) {
}