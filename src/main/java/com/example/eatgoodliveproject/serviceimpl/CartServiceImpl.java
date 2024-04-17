package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.dto.CartDto;
import com.example.eatgoodliveproject.dto.CartItemDto;
import com.example.eatgoodliveproject.dto.CartResponse;
import com.example.eatgoodliveproject.dto.ProductDto;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
import com.example.eatgoodliveproject.exception.UserNotFoundException;
import com.example.eatgoodliveproject.model.Cart;
import com.example.eatgoodliveproject.model.CartItem;
import com.example.eatgoodliveproject.model.Product;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.repositories.CartItemRepository;
import com.example.eatgoodliveproject.repositories.CartRepository;
import com.example.eatgoodliveproject.repositories.ProductRepository;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class CartServiceImpl implements CartService {
     private final ProductRepository productRepository;
     private final CartRepository cartRepository;
     private final UserRepository userRepository;

     @Autowired
    public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<CartResponse> addToCart(Long userId, Long productId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if(!product.isAvailable()){
            throw new ResourceNotFoundException("Out of Stock");
        }

        Cart cart = cartRepository.findByUser(user.getUsername());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user.getUsername());
        }

        List<CartItem> cartItems = cart.getCartItems();

        CartItem existingItem = findCartItemInCart(cart, product);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1L);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(1L);
            newCartItem.setCart(cart);
            newCartItem.setProductPrice(product.getPrice());
            cartItems.add(newCartItem);
            cart.setCartItems(cartItems);

        }

        Cart updateCart = updateTotalPriceAndTax(cart);


        Cart savedCart = cartRepository.save(updateCart);
        cartItems = savedCart.getCartItems();
        Set<CartItemDto> cartItemDtos = cartItems
                .stream()
                .map((item) -> new ObjectMapper().convertValue(item, CartItemDto.class)).collect(Collectors.toSet());


        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItemDtos(cartItemDtos);
        cartResponse.setQuantity(cartItems.size());
        cartResponse.setTax(savedCart.getTax());
        cartResponse.setSubtotalPrice(savedCart.getTotalPrice().subtract(savedCart.getTax()));
        cartResponse.setGrandTotal(savedCart.getTotalPrice());
        return ResponseEntity.ok(cartResponse);
    }

    @Override
    public ResponseEntity<?> removeFromCart(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        UserDetails user1 = (UserDetails) SecurityContextHolder.getContext().getAuthentication();

        Cart cart = cartRepository.findByUser(user1.getUsername());
        if (cart == null || cart.getCartItems().isEmpty()) {
             throw new ResourceNotFoundException("Cart is empty. Nothing to remove.");
        }
        CartItem existingItem = findCartItemInCart(cart, product);

        if (existingItem != null) {
            if (existingItem.getQuantity() > 1) {
                existingItem.setQuantity(existingItem.getQuantity() - 1L);
                return new ResponseEntity<> ("Quantity of existing cart item decreased to: " + existingItem.getQuantity(), HttpStatus.OK);
            } else {
                cart.getCartItems().remove(existingItem);
                 return new ResponseEntity<> ("Product removed from cart. Cart is now empty: " + cart.getCartItems().isEmpty(), HttpStatus.OK);
            }

        }
        Cart updateCart = updateTotalPriceAndTax(cart);


        Cart savedCart = cartRepository.save(updateCart);
        List<CartItem> cartItems = cart.getCartItems();
        Set<CartItemDto> cartItemDtos = cartItems.stream().map((item) -> new ObjectMapper().convertValue(item, CartItemDto.class)).collect(Collectors.toSet());


        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItemDtos(cartItemDtos);
        cartResponse.setQuantity(cartItems.size());
        cartResponse.setTax(savedCart.getTax());
        cartResponse.setSubtotalPrice(savedCart.getTotalPrice().subtract(savedCart.getTax()));
        cartResponse.setGrandTotal(savedCart.getTotalPrice());
        return ResponseEntity.ok(cartResponse);
    }

    private Cart updateTotalPriceAndTax(Cart cart) {
        BigDecimal subTotalPrice = BigDecimal.ZERO;
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotalPrice = cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subTotalPrice = subTotalPrice.add(itemTotalPrice);
        }
        double taxValue = 12.0/100.0;
        cart.setTax((BigDecimal.valueOf(taxValue)).multiply(subTotalPrice));
        cart.setTotalPrice(cart.getTax().add(subTotalPrice));
        System.out.println("\n");
        return cart;
    }

    private CartItem findCartItemInCart(Cart cart, Product product) {
        return cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }


    @Override
    public Map<Long, Integer> getCartItems(Long cartId) {
        return cartRepository.findById(cartId)
                .map(cart -> {
                    Map<Long, Integer> cartItems = new HashMap<>();
                    for (CartItem cartItem : cart.getCartItems()) {
                        Long productId = cartItem.getProduct().getId();
                        Integer quantity = Math.toIntExact(cartItem.getQuantity());
                        cartItems.put(productId, quantity);
                    }
                    return cartItems;
                })
                .orElse(Collections.emptyMap());
    }



    private void updateTotalPrice(Cart cart) {
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(cartItem -> cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
    }

    @Override
    public CartResponse getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart with ID: " + cartId + " not found."));

        List<CartItem> cartItems = cart.getCartItems();
        Set<CartItemDto> cartItemDtos = cartItems
                .stream()
                .map((item) -> new ObjectMapper().convertValue(item, CartItemDto.class)).collect(Collectors.toSet());


        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItemDtos(cartItemDtos);
        cartResponse.setQuantity(cartItems.size());
        cartResponse.setTax(cart.getTax());
        cartResponse.setSubtotalPrice(cart.getTotalPrice().subtract(cart.getTax()));
        cartResponse.setGrandTotal(cart.getTotalPrice());
        return cartResponse;
    }

    @Override
    public ResponseEntity<List<CartDto>> getAllCarts() {
        List<Cart> cartList = cartRepository.findAll();
        List<CartDto> cartDtos = cartList.stream()
                .map((item) -> new ObjectMapper().convertValue(item, CartDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(cartDtos);
    }

    @Override
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).get();
        clearCartItems(cart);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

    }

    private void clearCartItems(Cart cart) {
        cart.getCartItems().clear();
    }


}

