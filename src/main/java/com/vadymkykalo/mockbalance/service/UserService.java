package com.vadymkykalo.mockbalance.service;

import java.util.Map;

public interface UserService {

    void updateUserBalancesAsync(Map<Integer, Integer> balances);
}
