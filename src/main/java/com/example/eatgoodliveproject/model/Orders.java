package com.example.eatgoodliveproject.model;


import com.example.eatgoodliveproject.enums.TrackingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private Users user;


    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();


    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date orderDate;


    @JsonIgnore
    private TrackingStatus orderStatus;




    private BigDecimal totalPrice;


    @OneToOne
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    private Payment payment;
}
