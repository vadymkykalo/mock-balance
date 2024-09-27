package com.vadymkykalo.mockbalance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadymkykalo.mockbalance.dto.BalanceDto;
import com.vadymkykalo.mockbalance.entity.User;
import com.vadymkykalo.mockbalance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class UserBalanceFunctionalTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testUpdateUserBalancesWithLargeDatasetV1() throws Exception {
        int userCount = 100_000; // it also works for millions
        Map<Integer, Integer> balances = new HashMap<>();

        for (int i = 1; i <= userCount; i++) {
            userRepository.save(new User(i, "User" + i, 100));
            balances.put(i, i * 10);
        }

        BalanceDto requestDto = new BalanceDto(balances);

        mockMvc.perform(post("/api/v1/set-users-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted());

        // get random value
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> userRepository.findById(50_000).get().getBalance() == 500_000);

        User user50000 = userRepository.findById(50_000).orElseThrow();
        assertThat(user50000.getBalance()).isEqualTo(500_000);
    }

    @Test
    public void testUpdateSpecificUserBalancesV1() throws Exception {
        userRepository.save(new User(1, "User1", 100));
        userRepository.save(new User(2, "User2", 200));
        userRepository.save(new User(3, "User3", 300));

        Map<Integer, Integer> balances = new HashMap<>();
        balances.put(1, 500);
        balances.put(2, 600);
        balances.put(3, 700);

        BalanceDto requestDto = new BalanceDto(balances);

        mockMvc.perform(post("/api/v1/set-users-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted());

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> userRepository.findById(1).get().getBalance() == 500);

        User user1 = userRepository.findById(1).orElseThrow();
        User user2 = userRepository.findById(2).orElseThrow();
        User user3 = userRepository.findById(3).orElseThrow();

        assertThat(user1.getBalance()).isEqualTo(500);
        assertThat(user2.getBalance()).isEqualTo(600);
        assertThat(user3.getBalance()).isEqualTo(700);
    }
}
