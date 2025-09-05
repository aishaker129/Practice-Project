package com.campusKart.services;

import com.campusKart.auth.entity.User;
import com.campusKart.entity.*;
import com.campusKart.entity.Enum.OrderStatus;
import com.campusKart.entity.record.BuyerDTO;
import com.campusKart.entity.record.OrderItemDTO;
import com.campusKart.entity.record.OrderResponseDTO;
import com.campusKart.entity.record.SimpleUserDTO;
import com.campusKart.mapper.OrderMapper;
import com.campusKart.repository.CartRepo;
import com.campusKart.repository.OrderItemRepo;
import com.campusKart.repository.OrderRepo;
import com.campusKart.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepository;
    private final CartRepo cartRepo;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDTO placeOrder(User buyer) {
        Cart cart = cartRepo.findByBuyer(buyer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty())
            throw new RuntimeException("Cart is empty");

        Orders order = new Orders();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = cart.getItems().stream().map(ci -> {
            Product product = ci.getProduct();
            if (product.getStock() < ci.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getTitle());
            }

            product.setStock(product.getStock() - ci.getQuantity());
            productRepository.save(product);

            return new OrderItem(null, order, product,
                    ci.getQuantity(), ci.getPriceAtAddedTime(), ci.getVariant());
        }).toList();

        order.setItems(orderItems);

        // Save order and calculate total amount dynamically in DTO
        Orders savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepo.save(cart);

        return OrderMapper.toDTO(savedOrder);
    }

    public List<OrderResponseDTO> getOrders(User buyer) {
        List<Orders> orders = orderRepository.findByBuyer(buyer);
        return orders.stream()
                .map(OrderMapper::toDTO)
                .toList();
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        Orders updatedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(updatedOrder);
    }
}
