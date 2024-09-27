package com.vadymkykalo.mockbalance.service;

import com.vadymkykalo.mockbalance.entity.User;
import com.vadymkykalo.mockbalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
    private final PlatformTransactionManager transactionManager;

    public void updateUserBalances(Map<Integer, Integer> balances) {
        List<Integer> userIds = new ArrayList<>(balances.keySet());

        for (int i = 0; i < userIds.size(); i += batchSize) {
            List<Integer> batchUserIds = userIds.subList(i, Math.min(i + batchSize, userIds.size()));

            userBalanceExecutor.submit(() -> {
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("batchTransaction");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

                TransactionStatus status = transactionManager.getTransaction(def);

                try {
                    processBatch(batchUserIds, balances);
                    transactionManager.commit(status);
                } catch (Exception e) {
                    transactionManager.rollback(status);
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
