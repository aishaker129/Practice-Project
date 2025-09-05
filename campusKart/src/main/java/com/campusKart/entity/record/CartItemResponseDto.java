package com.campusKart.entity.record;

public record CartItemResponseDto(
        Long id,
        Long productId,
        String title,
        double price,
        int quantity,
        String imageUrl
) {
}
