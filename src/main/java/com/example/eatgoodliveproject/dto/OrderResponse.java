package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.ShippingMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private List<OrderDto> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private ShippingMethod shippingMethod;
    private Integer totalPages;
    private boolean lastPage;
}
