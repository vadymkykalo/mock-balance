package com.vadymkykalo.mockbalance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vadymkykalo.mockbalance.dto.UserBalancesDto;
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
    public void testSetUserBalancesResponseV1() throws Exception {

        Map<Integer, Integer> userIdBalance = new HashMap<>();
        userIdBalance.put(1, 500);
        userIdBalance.put(2, 600);
        userIdBalance.put(3, 700);

        UserBalancesDto requestDto = new UserBalancesDto(userIdBalance);

        doNothing().when(userService).updateUserBalancesAsync(Mockito.anyMap());

        mockMvc.perform(post("/api/v1/set-users-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Batch job started successfully, processing in background..."));
    }
}
