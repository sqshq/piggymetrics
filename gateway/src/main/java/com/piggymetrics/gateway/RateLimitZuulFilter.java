package com.piggymetrics.gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@ConditionalOnProperty(name = "ratelimit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitZuulFilter extends ZuulFilter {

    private final RateLimitProperties properties;
    private final TokenBucketRateLimiter rateLimiter;

    @Autowired
    public RateLimitZuulFilter(RateLimitProperties properties, TokenBucketRateLimiter rateLimiter) {
        this.properties = properties;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String method = request.getMethod();
        String path = request.getRequestURI();
        return method.equalsIgnoreCase("POST") && (path.startsWith("/accounts/") || path.equals("/accounts"));
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String ip = getClientIp(request);
        long refillIntervalMs = properties.getRefillSeconds() * 1000L;
        if (!rateLimiter.isAllowed(ip, refillIntervalMs, properties.getCapacity(),
                properties.getRefillTokens(), properties.getIpWhitespaceSeconds())) {
            ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            ctx.setResponseBody("{\"error\":\"rate_limit_exceeded\",\"message\":\"Too many requests. Try again later.\"}");
            ctx.getResponse().setContentType("application/json");
            ctx.setSendZuulResponse(false);
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "unknown";
    }
}
