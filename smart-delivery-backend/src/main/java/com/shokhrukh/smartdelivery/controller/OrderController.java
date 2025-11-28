package com.shokhrukh.smartdelivery.controller;

import com.shokhrukh.smartdelivery.dto.CreateOrderRequest;
import com.shokhrukh.smartdelivery.dto.OrderResponse;
import com.shokhrukh.smartdelivery.dto.OrderStatusHistoryResponse;
import com.shokhrukh.smartdelivery.dto.UpdateStatusRequest;
import com.shokhrukh.smartdelivery.security.JwtService;
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
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT')")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request, Authentication auth) {
        return orderService.createOrder(request, auth.getName());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('RIDER')") // Both can update
    public OrderResponse updateStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest req,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return orderService.updateOrderStatus(id, req.getStatus(), email);
    }

    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('RIDER')") // Both can view
    public List<OrderResponse> getOrders(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RESTAURANT"))
                        ? orderService.getOrdersForRestaurant(auth.getName())
                        : orderService.getOrdersForRider(auth.getName());
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('RIDER') == false") // only restaurant/admin
    public OrderResponse assign(
            @PathVariable String id,
            Authentication auth) {
        return orderService.assignOrderToRider(id, auth.getName());
    }

    @PostMapping("/{id}/picked-up")
    @PreAuthorize("hasAuthority('RIDER')")
    public OrderResponse pickedUp(
            @PathVariable String id,
            Authentication auth) {
        return orderService.riderPickUp(id, auth.getName());
    }

    @PostMapping("/{id}/delivered")
    @PreAuthorize("hasAuthority('RIDER')")
    public OrderResponse delivered(
            @PathVariable String id,
            Authentication auth) {
        return orderService.riderDeliver(id, auth.getName());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('RESTAURANT')")
    public OrderResponse cancel(
            @PathVariable String id,
            Authentication auth) {
        return orderService.cancelOrder(id, auth.getName());
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistoryResponse> history(@PathVariable String id) {
        return orderService.getHistory(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

}
