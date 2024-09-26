package com.vadymkykalo.mockbalance.repository;

import com.vadymkykalo.mockbalance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}

