package com.campusKart.services;

import com.campusKart.auth.entity.User;
import com.campusKart.auth.repository.UserRepo;
import com.campusKart.dto.ProductResponseDto;
import com.campusKart.entity.*;
import com.campusKart.entity.Enum.OrderStatus;
import com.campusKart.entity.Enum.PaymentMethod;
import com.campusKart.mapper.OrderMapper;
import com.campusKart.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductRepository productRepository;
    private final UserRepo userRepo;
    private final OrderMapper orderMapper;

    /**
     * Place an order from the user's cart
     */
    @Transactional
    public Orders placeOrder(Principal principal, PaymentMethod paymentMethod) {
        User buyer = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepo.findByBuyer(buyer)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Create new Order
        Orders order = new Orders();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(paymentMethod);
        order.setCreatedAt(LocalDateTime.now());

        Orders savedOrder = orderRepo.save(order);

        double totalAmount = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Check stock availability
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product: " + product.getTitle());
            }

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(product.getPrice() * cartItem.getQuantity());

            totalAmount += orderItem.getTotalPrice();
            orderItemRepo.save(orderItem);
        }

        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepo.save(savedOrder);

        // Clear cart after placing order
        cartItemRepo.deleteAll(cart.getCartItems());

        return savedOrder;
    }

    /**
     * Update order status through lifecycle
     */
    @Transactional
    public Orders updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        OrderStatus currentStatus = order.getStatus();

        // Validate transitions
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status transition: " + currentStatus + " → " + newStatus);
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepo.save(order);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.ACCEPTED;
            case ACCEPTED -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
    }

    /**
     * Get orders of current user
     */
    public List<?> getMyOrders(Principal principal) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Orders> orders = orderRepo.findByBuyerId(user.getId());
        return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }

    /**
     * Seller view: get all orders for their products
     */
    public List<?> getOrdersForSeller(Long sellerId) {
        List<Orders> orders = orderRepo.findOrdersBySellerId(sellerId);
        return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }
}
