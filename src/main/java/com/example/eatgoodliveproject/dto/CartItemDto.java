package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.model.Cart;
import com.example.eatgoodliveproject.model.Product;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long id;
    private Product product;
    private Long quantity;
    private BigDecimal productPrice;
}
