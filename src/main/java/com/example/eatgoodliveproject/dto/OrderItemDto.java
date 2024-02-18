package com.example.eatgoodliveproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long orderItemId;
    private ProductDto product;
    private Integer quantity;
    private BigDecimal orderedProductPrice;
}
