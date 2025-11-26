package com.shokhrukh.smartdelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shokhrukh.smartdelivery.entity.Order;
import com.shokhrukh.smartdelivery.entity.OrderStatusHistory;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, String> {
    List<OrderStatusHistory> findByOrderOrderByCreatedAtAsc(Order order);

}
