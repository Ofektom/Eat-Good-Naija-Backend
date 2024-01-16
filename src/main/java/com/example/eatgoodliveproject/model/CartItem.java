package com.example.eatgoodliveproject.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    private Long quantity;
    private BigDecimal productPrice;
}
