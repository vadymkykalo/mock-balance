package com.vadymkykalo.mockbalance.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${batch.size:1000}")
    private int batchSize;

    private final BatchProcessor batchProcessor;
    private final ExecutorService userBalanceExecutor;

    @Override
    public void updateUserBalancesAsync(Map<Integer, Integer> userIdBalance) {
        List<Integer> userIds = new ArrayList<>(userIdBalance.keySet());

        for (int i = 0; i < userIds.size(); i += batchSize) {
            List<Integer> batchUserIds = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

            userBalanceExecutor.submit(() -> batchProcessor.processBatch(batchUserIds, userIdBalance));
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        userBalanceExecutor.shutdown();
        try {
            if (!userBalanceExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                userBalanceExecutor.shutdownNow();
                if (!userBalanceExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("Executor is not terminate");
                }
            }
        } catch (InterruptedException ie) {
            userBalanceExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
