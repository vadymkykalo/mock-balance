package com.vadymkykalo.mockbalance.service;

import com.vadymkykalo.mockbalance.entity.User;
import com.vadymkykalo.mockbalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final UserRepository userRepository;
    private final ExecutorService userBalanceExecutor;

    @Transactional
    public void updateUserBalances(Map<Integer, Integer> balances) {
        List<Integer> userIds = new ArrayList<>(balances.keySet());

        for (int i = 0; i < userIds.size(); i += batchSize) {
            List<Integer> batchUserIds = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

            userBalanceExecutor.submit(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    processBatch(batchUserIds, balances);

                    long endTime = System.currentTimeMillis();
                    double duration = (endTime - startTime) / 1000.0;

                    log.info("Batch size {} processed in {} seconds", batchUserIds.size(), duration);
                } catch (Exception e) {
                    log.error("Error processing batch {}: {}", batchUserIds, e.getMessage());
                }
            });
        }
    }

    private void processBatch(List<Integer> batchUserIds, Map<Integer, Integer> balances) {
        List<User> usersBatch = userRepository.findAllById(batchUserIds);

        for (User user : usersBatch) {
            Integer newBalance = balances.get(user.getId());
            if (null != newBalance) {
                user.setBalance(newBalance);
            }
        }

        userRepository.saveAll(usersBatch);
        userRepository.flush();
    }
}
