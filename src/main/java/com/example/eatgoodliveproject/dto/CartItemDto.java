package com.example.eatgoodliveproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long Id;
    private CartDto cart;
    private ProductDto product;
    private Long quantity;
    private BigDecimal productPrice;
}
