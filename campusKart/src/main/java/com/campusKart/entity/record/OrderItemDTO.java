package com.campusKart.entity.record;

import com.campusKart.entity.Enum.ProductCategory;

public record OrderItemDTO(
        Long productId,
        String title,
        Double price,
        int quantity,
        ProductCategory productCategory,
        SimpleUserDTO postedBy
) {
}
