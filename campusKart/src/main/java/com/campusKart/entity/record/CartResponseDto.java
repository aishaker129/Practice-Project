package com.campusKart.entity.record;

import java.util.List;

public record CartResponseDto(
        Long id,
        Long buyerId,
        List<CartItemResponseDto> items,
        double totalPrice) {
}
