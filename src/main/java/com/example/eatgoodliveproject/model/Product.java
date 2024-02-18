package com.example.eatgoodliveproject.model;


import com.example.eatgoodliveproject.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private Category category;
    private Long size;
    private BigDecimal price;
    private String imageUrl;

    @JsonIgnore
    @Column(name = "favorite")
    private Integer favorite = 0;

    @JsonIgnore
    @Column(name = "isAvailable")
    private boolean isAvailable = false;

    @ManyToMany
    @JoinTable(name = "product_favorite", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private List<Users> favoriteBy = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Product(String name, Category category, Long size, BigDecimal price, String imageUrl, Users user){
        this.name = name;
        this.category = category;
        this.size = size;
        this.price = price;
        this.imageUrl = imageUrl;
        this.user = user;
    }
}
