package com.example.eatgoodliveproject.model;

import com.example.eatgoodliveproject.enums.RatingOptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String complaint;
    private RatingOptions ratings;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;
}
