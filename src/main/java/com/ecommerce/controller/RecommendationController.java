package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    @GetMapping("/personalized/{userId}")
    public ResponseEntity<List<Product>> getPersonalizedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getPersonalizedRecommendations(userId, limit));
    }
    
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<Product>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(recommendationService.getSimilarProducts(productId, limit));
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getPopularProducts(limit));
    }
    
    @GetMapping("/frequently-bought-together/{productId}")
    public ResponseEntity<List<Product>> getFrequentlyBoughtTogether(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(recommendationService.getFrequentlyBoughtTogether(productId, limit));
    }
}
