package com.vadymkykalo.mockbalance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${batch.size:1000}")
    private int batchSize;

    private final BatchProcessor batchProcessor;
    private final ExecutorService userBalanceExecutor;

    public void updateUserBalancesAsync(Map<Integer, Integer> balances) {
        List<Integer> userIds = new ArrayList<>(balances.keySet());

        for (int i = 0; i < userIds.size(); i += batchSize) {
            List<Integer> batchUserIds = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

            userBalanceExecutor.submit(() -> batchProcessor.processBatch(batchUserIds, balances));
        }
    }
}
