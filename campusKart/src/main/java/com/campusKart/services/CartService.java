package com.campusKart.services;

import com.campusKart.auth.entity.User;
import com.campusKart.entity.Cart;
import com.campusKart.entity.CartItem;
import com.campusKart.entity.Product;
import com.campusKart.entity.record.CartItemResponseDto;
import com.campusKart.entity.record.CartResponseDto;
import com.campusKart.repository.CartItemRepo;
import com.campusKart.repository.CartRepo;
import com.campusKart.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepo cartRepo;
    private final ProductRepository productRepository;
    private final CartItemRepo cartItemRepository;

    @Transactional
    public void addItemToCart(User buyer, Long productId, int qty, String variant) {
        Cart cart = cartRepo.findByBuyer(buyer)
                .orElseGet(() -> cartRepo.save(new Cart(null, buyer, new ArrayList<>())));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < qty) throw new RuntimeException("Not enough stock");

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId) &&
                        Objects.equals(ci.getVariant(), variant))
                .findFirst()
                .orElse(null);

        if (item != null) {
            int newQty = item.getQuantity() + qty;
            if (newQty > product.getStock()) throw new RuntimeException("Exceeds stock");
            item.setQuantity(newQty);
            item.setVariant(variant);
        } else {
            item = new CartItem(null, cart, product, (int) product.getPrice(),qty, variant);

            cart.getItems().add(item);
        }

        cartRepo.save(cart);
    }

    @Transactional
    public void updateQuantity(User buyer, Long itemId, int qty) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (item.getProduct().getStock() < qty) throw new RuntimeException("Not enough stock");
        item.setQuantity(qty);
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(User buyer, Long itemId) {
        Cart cart = cartRepo.findByBuyer(buyer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepo.save(cart);
    }

    @Transactional
    public void clearCart(User buyer) {
        Cart cart = cartRepo.findByBuyer(buyer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepo.save(cart);
    }

    public Cart getCart(User buyer) {
        return cartRepo.findByBuyer(buyer)
                .orElseGet(() -> cartRepo.save(new Cart(null, buyer, new ArrayList<>())));
    }

    public CartResponseDto getCartDto(User buyer) {
        Cart cart = getCart(buyer);

        List<CartItemResponseDto> itemDtos = cart.getItems().stream()
                .map(item -> new CartItemResponseDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getTitle(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getImageUrl()
                ))
                .toList();

        double totalPrice = itemDtos.stream()
                .mapToDouble(i -> i.price() * i.quantity())
                .sum();

        return new CartResponseDto(
                cart.getId(),
                cart.getBuyer().getId(),
                itemDtos,
                totalPrice
        );
    }

}
