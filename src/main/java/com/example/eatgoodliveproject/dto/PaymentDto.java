package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.PaymentMethod;
import com.example.eatgoodliveproject.enums.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private PaymentMethod paymentMethod;
}
