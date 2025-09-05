package com.campusKart.controller;

import com.campusKart.auth.entity.User;
import com.campusKart.auth.services.ProfileService;
import com.campusKart.entity.Cart;
import com.campusKart.entity.record.CartResponseDto;
import com.campusKart.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ProfileService profileService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long productId,
                                       @RequestParam int qty,
                                       @RequestParam(required = false) String variant,
                                       Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        cartService.addItemToCart(buyer, productId, qty, variant);
        return ResponseEntity.ok("Item added to cart");
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long itemId,
                                            @RequestParam int qty,
                                            Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        cartService.updateQuantity(buyer, itemId, qty);
        return ResponseEntity.ok("Quantity updated");
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId, Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        cartService.removeItem(buyer, itemId);
        return ResponseEntity.ok("Item removed");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        cartService.clearCart(buyer);
        return ResponseEntity.ok("Cart cleared");
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(Principal principal) {
        User buyer = profileService.getCurrentUser(principal);
        return ResponseEntity.ok(cartService.getCartDto(buyer));
    }
}
