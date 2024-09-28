package com.vadymkykalo.mockbalance.service;

import com.vadymkykalo.mockbalance.entity.User;
import com.vadymkykalo.mockbalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessorImpl implements BatchProcessor {

    private final UserRepository userRepository;
    private final PlatformTransactionManager transactionManager;

    @Retryable(
            value = { Exception.class },
            exclude = {DataIntegrityViolationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void processBatch(List<Integer> batchUserIds, Map<Integer, Integer> balances) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("batchTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            List<User> usersBatch = userRepository.findAllById(batchUserIds);
            for (User user : usersBatch) {
                Integer newBalance = balances.get(user.getId());
                if (newBalance != null) {
                    user.setBalance(newBalance);
                }
            }
            userRepository.saveAll(usersBatch);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("Error processing batch {}: {}", batchUserIds, e.getMessage());
            throw e;
        }
    }

    @Recover
    public void recover(Exception e, Map<Integer, Integer> balances) {
        log.error("Retries exhausted for balances: {}", balances);
    }
}
