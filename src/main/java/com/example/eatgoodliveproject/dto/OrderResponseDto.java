package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.PaymentMethod;
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
public class OrderResponseDto {
    private Long orderId;
    private String paymentMethod;
    private int orderListSize;
    private List<String> productNames;
    private List<BigDecimal> orderItemPrices;
    private BigDecimal totalPrice;
    private BigDecimal tax;
    private BigDecimal grandTotal;
}
