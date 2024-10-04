package com.vadymkykalo.mockbalance.service;

import com.vadymkykalo.mockbalance.entity.User;
import com.vadymkykalo.mockbalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessorImpl implements BatchProcessor {

    private final UserRepository userRepository;

    @Retryable(
            value = {Exception.class},
            exclude = {DataIntegrityViolationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Transactional
    @Override
    public void processBatch(List<Integer> batchUserIds, Map<Integer, Integer> userIdBalance) {
        try {
            for (Integer userId : batchUserIds) {
                Integer newBalance = userIdBalance.get(userId);
                if (newBalance != null) {
                    userRepository.updateUserBalance(userId, newBalance);
                }
            }
        } catch (Exception e) {
            log.error("Error processing batch ... Message: {}", e.getMessage());
            throw e;
        }
    }
}
