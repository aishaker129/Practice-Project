package com.campusKart.repository;

import com.campusKart.auth.entity.User;
import com.campusKart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyer(User buyer);
}
