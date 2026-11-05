package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.UpdateCartItemRequest;
import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }
    
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
        ));
    }
    
    @PutMapping("/update")
    public ResponseEntity<Cart> updateCartItem(@RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(
                request.getUserId(),
                request.getItemId(),
                request.getQuantity()
        ));
    }
    
    @DeleteMapping("/{userId}/item/{itemId}")
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
