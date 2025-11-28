package com.shokhrukh.smartdelivery.entity;

import com.shokhrukh.smartdelivery.enums.OrderStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "changed_by_user_id")
    private User changedByUser;

}
