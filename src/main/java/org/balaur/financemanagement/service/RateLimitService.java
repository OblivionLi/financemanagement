package org.balaur.financemanagement.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimitService {
    private final Map<String, Integer> requestCounts = new HashMap<>();
    private static final int MAX_DAILY_REQUESTS = 5;

    public boolean isRateLimitRequest(String userEmail) {
        int requests = requestCounts.getOrDefault(userEmail, 0);
        return requests >= MAX_DAILY_REQUESTS;
    }

    public void incrementRequestCount(String userEmail) {
        requestCounts.put(userEmail, requestCounts.getOrDefault(userEmail, 0) + 1);
    }

    public void resetDailyLimits() {
        requestCounts.clear();
    }
}
