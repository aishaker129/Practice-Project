package com.campusKart.dto;

import com.campusKart.entity.Enum.ProductCategory;
import com.campusKart.entity.Enum.ProductStatus;
import com.campusKart.entity.Enum.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String imageUrl;
    private boolean sold;
    private int stock;
    private String condition;
    private ProductCategory productCategory;
    private ProductType productType;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private String postedBy;
}
