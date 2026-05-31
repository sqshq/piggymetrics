package com.piggymetrics.gateway;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBucketRateLimiter {

    private static class Bucket {
        final long refillIntervalMs;
        final int capacity;
        final int refillTokens;
        long lastRefill;
        double tokens;

        Bucket(long refillIntervalMs, int capacity, int refillTokens) {
            this.refillIntervalMs = refillIntervalMs;
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.lastRefill = System.currentTimeMillis();
            this.tokens = capacity;
        }

        synchronized boolean tryConsume() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefill;
            if (elapsed >= refillIntervalMs) {
                long periods = elapsed / refillIntervalMs;
                tokens = Math.min(capacity, tokens + periods * refillTokens);
                lastRefill = now;
            }
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private volatile long lastCleanup = System.currentTimeMillis();

    public boolean isAllowed(String key, long refillIntervalMs, int capacity, int refillTokens, int ipWhitespaceSeconds) {
        long now = System.currentTimeMillis();
        if (now - lastCleanup > ipWhitespaceSeconds * 1000L) {
            cleanup(now, ipWhitespaceSeconds * 1000L);
            lastCleanup = now;
        }
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(refillIntervalMs, capacity, refillTokens));
        return bucket.tryConsume();
    }

    private void cleanup(long now, long maxIdleMs) {
        buckets.entrySet().removeIf(e -> (now - e.getValue().lastRefill) > maxIdleMs);
    }
}
