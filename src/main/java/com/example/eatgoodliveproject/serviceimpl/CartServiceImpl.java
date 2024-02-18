package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.dto.CartDto;
import com.example.eatgoodliveproject.dto.ProductDto;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class CartServiceImpl implements CartService {
     private final ProductRepository productRepository;
     private final CartItemRepository cartItemRepository;
     private final CartRepository cartRepository;
     private final UserRepository userRepository;

     @Autowired
    public CartServiceImpl(ProductRepository productRepository, CartItemRepository cartItemRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }


    @Override
    public CartDto addProductToCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty" + cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        // Check if the product is already in the cart
        CartItem existingItem = findCartItemInCart(cart, product);

        if (existingItem != null) {
            // If the product is already in the cart, increment the quantity
            existingItem.setQuantity(existingItem.getQuantity() + 1L);
            existingItem.setProductPrice(existingItem.getProductPrice());
            cart.getCartItems().add(existingItem);
            log.info("Price of old cart item: " + cart.getTotalPrice());
        } else {
            // If the product is not in the cart, create a new CartItem
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(1L);
            newCartItem.setCart(cart);
            newCartItem.setProductPrice(product.getPrice());

            cartItemRepository.save(newCartItem);
            cart.getCartItems().add(newCartItem);
            log.info("Price of new Product: " + product.getPrice() + ". Price of cartItem" + cart.getTotalPrice());
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            BigDecimal itemPrice = cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
        }

        CartDto cartDTO = new CartDto();

        List<ProductDto> productDTOs = cart.getCartItems()
                .stream()
                .map(p -> new ObjectMapper().convertValue(p.getProduct(), ProductDto.class))
                .collect(Collectors.toList());
        cartDTO.setProducts(productDTOs);
        cartDTO.setTotalPrice(totalPrice);

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        return cartDTO;
    }


    @Override
    public ResponseEntity<String> addToCart(Long userId, Long productId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if(!product.isAvailable()){
            return new ResponseEntity<>("Out of stock", HttpStatus.BAD_REQUEST);
        }

        Cart cart = new Cart();
        cart.setUser(user.getUsername());



        // Check if the product is already in the cart
        CartItem existingItem = findCartItemInCart(cart, product);

        if (existingItem != null) {
            // If the product is already in the cart, increment the quantity
            existingItem.setQuantity(existingItem.getQuantity() + 1L);
            cart.setTotalPrice(cart.getTotalPrice().add(existingItem.getProductPrice()));
            cart.getCartItems().add(existingItem);
            log.info("Price of old cart item: " + cart.getTotalPrice());
        } else {
            // If the product is not in the cart, create a new CartItem
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setQuantity(1L);
            newCartItem.setCart(cart);
            newCartItem.setProductPrice(product.getPrice());

            cartItemRepository.save(newCartItem);
            cart.setTotalPrice(cart.getTotalPrice().add(newCartItem.getProductPrice()));
//            cart.setTotalPrice(cart.getTotalPrice()==null?newCartItem.getProductPrice():cart.getTotalPrice().add(newCartItem.getProductPrice()));
            cart.getCartItems().add(newCartItem);
            log.info("Price of new Product: " + product.getPrice() + ". Price of cartItem" + cart.getTotalPrice());
        }

        Cart cart1 = updateCartTotalPrice(cart);


        cartRepository.save(cart1);
        userRepository.save(user);
        return ResponseEntity.ok("Product added to the shopping cart successfully.");
    }

    @Override
    public ResponseEntity<?> removeFromCart(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UserDetails user1 = (UserDetails) SecurityContextHolder.getContext().getAuthentication();

        Cart cart = cartRepository.findByUser(user1.getUsername());
        if (cart == null || cart.getCartItems().isEmpty()) {
            return new ResponseEntity<>("Cart is empty. Nothing to remove.", HttpStatus.NOT_FOUND);
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
        Cart cart1 = updateCartTotalPrice(cart);


        cartRepository.save(cart1);
        return new ResponseEntity<> ("Product not found in the cart. Nothing to remove.", HttpStatus.NOT_FOUND);
    }

    private Cart updateCartTotalPrice(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            BigDecimal itemPrice = cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
        }
        cart.setTotalPrice(totalPrice);
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
    public CartDto getCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with ID: " + cartId + " not found."));
        return new ObjectMapper().convertValue(cart, CartDto.class);
    }

    @Override
    public ResponseEntity<List<CartDto>> getCart() {
        List<Cart> cartList = cartRepository.findAll();
        List<CartDto> cartDtos = cartList.stream().map(cart -> {
            CartDto cartDTO = new ObjectMapper().convertValue(cart, CartDto.class);
            List<ProductDto> products = cart.getCartItems().stream()
                    .map(p -> new ObjectMapper().convertValue(p.getProduct(), ProductDto.class)).collect(Collectors.toList());
            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());
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

