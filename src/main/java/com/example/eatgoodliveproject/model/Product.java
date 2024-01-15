package com.example.eatgoodliveproject.model;

import com.example.eatgoodliveproject.enums.DishType;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Product {
    private Long id;
    private String dish;
    private DishType category;
    private BigDecimal size;
    private String dishImageUrl;
    private double price;
}
