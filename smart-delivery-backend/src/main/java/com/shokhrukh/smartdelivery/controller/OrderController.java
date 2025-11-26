package com.shokhrukh.smartdelivery.controller;

import com.shokhrukh.smartdelivery.dto.CreateOrderRequest;
import com.shokhrukh.smartdelivery.dto.OrderResponse;
import com.shokhrukh.smartdelivery.enums.Role;
import com.shokhrukh.smartdelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('RESTAURANT')")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request, Authentication auth) {
        return orderService.createOrder(request, auth.getName());
    }

    @GetMapping
    public List<OrderResponse> getOrders(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.RESTAURANT.name()))
                ? orderService.getOrdersForRestaurant(auth.getName())
                : orderService.getOrdersForRider(auth.getName());
    }

}
