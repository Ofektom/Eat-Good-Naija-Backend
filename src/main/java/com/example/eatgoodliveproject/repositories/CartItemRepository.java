package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.model.Cart;
import com.example.eatgoodliveproject.model.CartItem;
import com.example.eatgoodliveproject.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    Set<CartItem> findAllByCart(Cart car);

    CartItem findByCartAndProduct(Cart cart, Product product);

    CartItem findByCartAndId(Cart cart, Long id);
}
