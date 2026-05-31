package com.piggymetrics.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {

    private int capacity = 10;
    private int refillTokens = 1;
    private int refillSeconds = 1;
    private int ipWhitespaceSeconds = 60;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRefillTokens() {
        return refillTokens;
    }

    public void setRefillTokens(int refillTokens) {
        this.refillTokens = refillTokens;
    }

    public int getRefillSeconds() {
        return refillSeconds;
    }

    public void setRefillSeconds(int refillSeconds) {
        this.refillSeconds = refillSeconds;
    }

    public int getIpWhitespaceSeconds() {
        return ipWhitespaceSeconds;
    }

    public void setIpWhitespaceSeconds(int ipWhitespaceSeconds) {
        this.ipWhitespaceSeconds = ipWhitespaceSeconds;
    }
}
