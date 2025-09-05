package com.campusKart.dto;

import com.campusKart.entity.Enum.ProductCategory;
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
public class ProductRequestDto {
    private String title;
    private String description;
    private double price;
    private String condition;
    private int stock;
    private ProductCategory productCategory;
    private ProductType productType;
    private LocalDateTime createdAt;
}
