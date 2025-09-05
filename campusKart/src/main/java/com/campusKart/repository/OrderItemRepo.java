package com.campusKart.repository;

import com.campusKart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findById(Long id);

}
