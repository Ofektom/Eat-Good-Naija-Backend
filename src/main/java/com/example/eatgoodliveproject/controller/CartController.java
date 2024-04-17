package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.dto.CartDto;
import com.example.eatgoodliveproject.dto.CartItemDto;
import com.example.eatgoodliveproject.dto.CartResponse;
import com.example.eatgoodliveproject.service.CartService;
import com.example.eatgoodliveproject.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    public final CartService cartService;
    public final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/adding-to-cart/{userId}/{productId}")
    public ResponseEntity<CartResponse> addToCart(@PathVariable Long userId, @PathVariable Long productId) {
        return cartService.addToCart(userId, productId);
    }

    @DeleteMapping("/remove-from-cart/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId){
        return cartService.removeFromCart(productId);
    }


    @GetMapping("/all")
    public ResponseEntity<List<CartDto>> getAllCarts(){
        return cartService.getAllCarts();
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long cartId) {
        CartResponse cartResponse = cartService.getCartById(cartId);
        return ResponseEntity.ok(cartResponse);

    }

    @GetMapping("/items/{cartId}")
    public ResponseEntity<Map<Long, Integer>> getCartItems(@PathVariable Long cartId) {
        Map<Long, Integer> cartItems = cartService.getCartItems(cartId);
        return ResponseEntity.ok(cartItems);

    }
}
