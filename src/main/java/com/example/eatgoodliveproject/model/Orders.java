package com.example.eatgoodliveproject.model;


import com.example.eatgoodliveproject.enums.ShippingMethod;
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



    @ManyToOne
    @JoinColumn(name = "user_name")
    @JsonIgnore
    private Users user;


    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();


    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date orderDate;

    @JsonIgnore
    @Column(name = "isReceived")
    private boolean isReceived = false;
    @JsonIgnore
    @Column(name = "isPrepared")
    private boolean isPrepared = false;
    @JsonIgnore
    @Column(name = "isReaady")
    private boolean isReady = false;
    @JsonIgnore
    @Column(name = "inTransit")
    private boolean inTransit = false;
    @JsonIgnore
    @Column(name = "isDelivered")
    private boolean isDelivered = false;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Review> reviews;

    @JsonIgnore
    private BigDecimal totalPrice;

    @JsonIgnore
    private ShippingMethod shippingMethod;


    @OneToOne
    @JoinColumn(name = "payment_id")
    @JsonIgnore
    private PaymentPaystack payment;
}
