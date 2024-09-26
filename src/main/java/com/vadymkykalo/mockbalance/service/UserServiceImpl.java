package com.vadymkykalo.mockbalance.service;

import com.vadymkykalo.mockbalance.entity.User;
import com.vadymkykalo.mockbalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserBalances(Map<Integer, Integer> balances) {
        for (Map.Entry<Integer, Integer> entry : balances.entrySet()) {
            Integer userId = entry.getKey();
            Integer newBalance = entry.getValue();

            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setBalance(newBalance);
                userRepository.save(user);
            }
        }
    }
}
