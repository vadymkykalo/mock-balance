package com.vadymkykalo.mockbalance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadymkykalo.mockbalance.dto.UserBalanceRequestDto;
import com.vadymkykalo.mockbalance.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserBalanceControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void testSetUserBalancesResponse() throws Exception {

        Map<Integer, Integer> balances = new HashMap<>();
        balances.put(1, 500);
        balances.put(2, 600);
        balances.put(3, 700);

        UserBalanceRequestDto requestDto = new UserBalanceRequestDto(balances);

        doNothing().when(userService).updateUserBalances(Mockito.anyMap());

        mockMvc.perform(post("/api/v1/set-users-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("User balance was started, processing in background..."));
    }
}
