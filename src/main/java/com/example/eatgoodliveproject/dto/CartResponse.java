package com.example.eatgoodliveproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Set<CartItemDto> cartItemDtos;
    private int quantity;
    private BigDecimal subtotalPrice;
    private BigDecimal tax;
    private BigDecimal grandTotal;
}
