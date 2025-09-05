package com.campusKart.entity.record;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        BuyerDTO buyer,
        String status,
        LocalDateTime createdAt,
        List<OrderItemDTO> items,
        double totalAmmount
) {
}
