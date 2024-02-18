package com.example.eatgoodliveproject.service;

import com.example.eatgoodliveproject.dto.CartDto;
import com.example.eatgoodliveproject.dto.CartItemDto;
import com.example.eatgoodliveproject.model.Cart;
import com.example.eatgoodliveproject.model.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CartService {


    ResponseEntity<String> addToCart(Long userId, Long productId);
    ResponseEntity<?> removeFromCart(Long productId);
    Map<Long, Integer> getCartItems(Long cartId);
//    CartDto findCartByIdAndUser(Long cartId, Users user);
    void clearCart(Long cartId);
    CartDto getCartById(Long cartId);

    ResponseEntity<List<CartDto>> getCart();

    CartDto addProductToCart(Long userId, Long productId);
}
