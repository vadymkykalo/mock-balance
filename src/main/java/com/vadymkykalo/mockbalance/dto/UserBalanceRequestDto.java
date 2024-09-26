package com.vadymkykalo.mockbalance.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Data
public class UserBalanceRequestDto {
    @NotEmpty
    private Map<Integer, Integer> balances;
}
