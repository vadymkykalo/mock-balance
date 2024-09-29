package com.vadymkykalo.mockbalance.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalancesDto {
    @NotEmpty
    private Map<Integer, Integer> userIdBalanceValue;
}
