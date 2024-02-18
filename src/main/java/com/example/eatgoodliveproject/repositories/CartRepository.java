package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.dto.CartDto;
import com.example.eatgoodliveproject.model.Cart;
import com.example.eatgoodliveproject.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart, Long> {
//    Cart findCartByIdAndUser(Long cartId, Users user);

    Cart findByUser(String user);

}
