package com.vadymkykalo.mockbalance.service;

import java.util.Map;

public interface UserService {

    void updateUserBalances(Map<Integer, Integer> balances);
}
