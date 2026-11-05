package com.piggymetrics.serverless;

@FunctionalInterface
public interface ServiceTokenProvider {
    String getToken();
}
