package com.vadymkykalo.mockbalance.repository;

import com.vadymkykalo.mockbalance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Modifying
    @Query("UPDATE User u SET u.balance = :newBalance WHERE u.id = :userId")
    void updateUserBalance(@Param("userId") Integer userId, @Param("newBalance") Integer newBalance);
}

