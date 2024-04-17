package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {
    private Long id;
    private String user;
    private List<CartItemDto> cartItems = new ArrayList<>();
    private BigDecimal tax;
    private BigDecimal totalPrice;
}
