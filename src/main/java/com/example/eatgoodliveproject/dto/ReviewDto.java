package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.RatingOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private String complaint;
    private RatingOptions ratings;
}
