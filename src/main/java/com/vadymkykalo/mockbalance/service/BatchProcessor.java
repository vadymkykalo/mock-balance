package com.vadymkykalo.mockbalance.service;

import java.util.List;
import java.util.Map;

public interface BatchProcessor {
    void processBatch(List<Integer> batchUserIds, Map<Integer, Integer> userIdBalance);
}
