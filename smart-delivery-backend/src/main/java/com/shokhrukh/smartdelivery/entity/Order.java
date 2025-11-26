package com.shokhrukh.smartdelivery.entity;

import com.shokhrukh.smartdelivery.enums.OrderStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private User restaurant;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private User rider;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private String dropoffAddress;

    private String customerName;
    private String customerPhone;

    private String notes;
}
