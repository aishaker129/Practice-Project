package com.campusKart.mapper;

import com.campusKart.entity.OrderItem;
import com.campusKart.entity.Orders;
import com.campusKart.entity.Product;
import com.campusKart.entity.record.BuyerDTO;
import com.campusKart.entity.record.OrderItemDTO;
import com.campusKart.entity.record.OrderResponseDTO;
import com.campusKart.entity.record.SimpleUserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Orders order) {
        BuyerDTO buyerDTO = new BuyerDTO(
                order.getBuyer().getId(),
                order.getBuyer().getName(),
                order.getBuyer().getEmail()
        );

        List<OrderItemDTO> items = order.getItems().stream()
                .map(OrderMapper::toOrderItemDTO)
                .collect(Collectors.toList());

        double totalAmount = order.getItems().stream()
                .mapToDouble(item -> item.getPriceAtOrderTime() * item.getQuantity())
                .sum();

        return new OrderResponseDTO(
                order.getId(),
                buyerDTO,
                order.getStatus().name(),
                order.getCreatedAt(),
                items,
                totalAmount
        );
    }

    private static OrderItemDTO toOrderItemDTO(OrderItem item) {
        Product p = item.getProduct();
        SimpleUserDTO postedBy = new SimpleUserDTO(
                p.getPostedBy().getId(),
                p.getPostedBy().getName()
        );

        return new OrderItemDTO(
                p.getId(),
                p.getTitle(),
                item.getPriceAtOrderTime(),
                item.getQuantity(),
                p.getProductCategory(),
                postedBy
        );
    }
}
