package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.OrderStatus;
import com.example.eatgoodliveproject.enums.ShippingMethod;
import com.example.eatgoodliveproject.enums.TrackingStatus;
import com.example.eatgoodliveproject.model.OrderItem;
import com.example.eatgoodliveproject.model.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Users user;
    private List<OrderItemDto> orderItems = new ArrayList<>();
    private Date orderDate;
    private BigDecimal totalPrice;
    private ShippingMethod shippingMethod;
    private OrderStatus orderStatus;
    private boolean isReceived;
    private boolean isPrepared;
    private boolean isReady;
    private boolean inTransit;
    private boolean isDelivered;

}
