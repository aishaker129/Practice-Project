package com.campusKart.repository;

import com.campusKart.auth.entity.User;
import com.campusKart.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepo extends JpaRepository<Orders, Long> {
    List<Orders> findByBuyerId(Long buyerId);
    List<Orders> findByBuyer(User sellerId);

    @Query("""
        SELECT DISTINCT o FROM Orders o
        JOIN o.items i
        WHERE i.product.postedBy.id = :sellerId
    """)
    List<Orders> findOrdersBySellerId(Long sellerId);
}
