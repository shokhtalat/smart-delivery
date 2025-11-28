package com.shokhrukh.smartdelivery.service;

import com.shokhrukh.smartdelivery.dto.CreateOrderRequest;
import com.shokhrukh.smartdelivery.dto.OrderResponse;
import com.shokhrukh.smartdelivery.entity.Order;
import com.shokhrukh.smartdelivery.entity.User;
import com.shokhrukh.smartdelivery.enums.OrderStatus;
import com.shokhrukh.smartdelivery.repository.OrderRepository;
import com.shokhrukh.smartdelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderResponse createOrder(CreateOrderRequest request, String restaurantEmail) {
        System.out.println("AUTH NAME = " + restaurantEmail);
        User restaurant = userRepository.findByEmail(restaurantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setDropoffAddress(request.getDropoffAddress());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);
        order.setRestaurant(restaurant);

        orderRepository.save(order);

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersForRestaurant(String email) {
        User restaurant = userRepository.findByEmail(email).orElseThrow();

        return orderRepository.findByRestaurant(restaurant)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersForRider(String email) {
        User rider = userRepository.findByEmail(email).orElseThrow();
        return orderRepository.findByRider(rider)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .customerName(o.getCustomerName())
                .customerPhone(o.getCustomerPhone())
                .dropoffAddress(o.getDropoffAddress())
                .notes(o.getNotes())
                .status(o.getStatus())
                .restaurantName(o.getRestaurant().getName())
                .riderName(o.getRider() != null ? o.getRider().getName() : null)
                .build();
    }

}
