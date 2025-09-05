package com.campusKart.entity;

import com.campusKart.auth.entity.User;
import com.campusKart.entity.Enum.ProductCategory;
import com.campusKart.entity.Enum.ProductStatus;
import com.campusKart.entity.Enum.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private double price;

    private String imageUrl;

    private String imagePublicId;

    private boolean sold;

    @Column(name = "product_condition")  // ✅ avoid reserved word
    private String condition;

    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.AVAILABLE;

    @Version
    private Long version; // optimistic locking

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType; // sale, rent, swap, donate

    @ManyToOne
    private User owner;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User postedBy;
}
