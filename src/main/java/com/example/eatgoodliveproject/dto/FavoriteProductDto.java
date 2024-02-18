package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProductDto {
    private String name;
    private Category category;
    private BigDecimal price;
    private Long size;
    private Integer favorites;
    private boolean isFavorite;
}
