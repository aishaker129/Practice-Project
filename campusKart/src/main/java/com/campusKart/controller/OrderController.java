package com.campusKart.controller;

import com.campusKart.auth.entity.User;
import com.campusKart.auth.services.ProfileService;
import com.campusKart.entity.Enum.OrderStatus;
import com.campusKart.entity.Orders;
import com.campusKart.entity.record.OrderResponseDTO;
import com.campusKart.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProfileService profileService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        return ResponseEntity.ok(orderService.placeOrder(buyer));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        return ResponseEntity.ok(orderService.getOrders(buyer));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Order status updated");
    }
}
