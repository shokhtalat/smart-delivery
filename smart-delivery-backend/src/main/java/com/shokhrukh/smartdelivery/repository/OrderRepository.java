package com.shokhrukh.smartdelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shokhrukh.smartdelivery.enums.OrderStatus;
import com.shokhrukh.smartdelivery.model.Order;
import com.shokhrukh.smartdelivery.model.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByRestaurant(User restaurant);

    List<Order> findByRider(User rider);

    List<Order> findByStatus(OrderStatus status);
}
