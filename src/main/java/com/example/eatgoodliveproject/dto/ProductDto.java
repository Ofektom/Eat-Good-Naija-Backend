package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto{
    private Long id;
    private String name;
    private Category category;
    private Long size;
    private String imageUrl;
    private BigDecimal price;

}
