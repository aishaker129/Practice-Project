package com.campusKart.controller;

import com.campusKart.entity.Enum.OrderStatus;
import com.campusKart.entity.Enum.PaymentMethod;
import com.campusKart.entity.Orders;
import com.campusKart.entity.record.OrderResponseDTO;
import com.campusKart.mapper.OrderMapper;
import com.campusKart.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    /**
     * Place an order from the user's cart
     */
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestParam PaymentMethod paymentMethod,
            Principal principal) {

        Orders savedOrder = orderService.placeOrder(principal, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toDTO(savedOrder));  // use instance method
    }

    /**
     * Update order status (seller/admin)
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        Orders updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toDTO(updatedOrder));
    }

    /**
     * Get orders of the current logged-in user
     */
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Principal principal) {
        List<OrderResponseDTO> orders = (List<OrderResponseDTO>) orderService.getMyOrders(principal);
        return ResponseEntity.ok(orders);
    }

    /**
     * Seller view: get orders for products they posted
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersForSeller(@PathVariable Long sellerId) {
        List<OrderResponseDTO> orders = (List<OrderResponseDTO>) orderService.getOrdersForSeller(sellerId);
        return ResponseEntity.ok(orders);
    }
}
