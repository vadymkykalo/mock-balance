package com.vadymkykalo.mockbalance;

import com.vadymkykalo.mockbalance.repository.UserRepository;
import com.vadymkykalo.mockbalance.service.BatchProcessorImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
public class BatchProcessorImplTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private BatchProcessorImpl batchProcessor;

    @Test
    void testRetryableAndRecovery() {
        Mockito.doThrow(new RuntimeException("Test Exception")).when(userRepository).saveAll(anyList());

        try {
            batchProcessor.processBatch(List.of(1, 2, 3), Map.of(1, 100, 2, 200));
        } catch (Exception e) {
            // The exception is expected
        }

        verify(userRepository, times(3)).saveAll(anyList());
        verify(userRepository, times(3)).findAllById(anyList());
    }
}