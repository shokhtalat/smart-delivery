package com.shokhrukh.smartdelivery.service;

import com.shokhrukh.smartdelivery.dto.CreateOrderRequest;
import com.shokhrukh.smartdelivery.dto.OrderResponse;
import com.shokhrukh.smartdelivery.dto.OrderStatusHistoryResponse;
import com.shokhrukh.smartdelivery.entity.Order;
import com.shokhrukh.smartdelivery.entity.OrderStatusHistory;
import com.shokhrukh.smartdelivery.entity.User;
import com.shokhrukh.smartdelivery.enums.Role;
import com.shokhrukh.smartdelivery.enums.OrderStatus;
import com.shokhrukh.smartdelivery.repository.OrderRepository;
import com.shokhrukh.smartdelivery.repository.OrderStatusHistoryRepository;
import com.shokhrukh.smartdelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStatusHistoryRepository historyRepository;

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

    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus, String email) {

        // 1. Load der
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        // 2. Loa User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // 3. Role-based permissions
        if (user.getRole() == Role.RIDER) {
            if (order.getRider() == null || !order.getRider().getEmail().equals(email)) {
                throw new RuntimeException("You are not assigned to this order.");
            }
        }

        if (user.getRole() == Role.RESTAURANT) {
            if (order.getRider() != null) {
                throw new RuntimeException("Order already assigned. Only rider can update status");
            }
        }

        // 4. update order
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // 5. Save status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setChangedByUser(user);
        history.setCreatedAt(LocalDateTime.now());
        historyRepository.save(history);
        // 6. return DTO
        return toResponse(order);
    }

    @Transactional
    public OrderResponse assignOrderToRider(String orderId, String riderEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be assigned.");
        }

        User rider = userRepository.findByEmail(riderEmail).orElseThrow();

        order.setRider(rider);
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        saveHistory(order, rider);

        return toResponse(order);
    }

    @Transactional
    public OrderResponse riderPickUp(String orderId, String riderEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getRider().getEmail().equals(riderEmail)) {
            throw new IllegalStateException("This rider is not assigned.");
        }

        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);

        saveHistory(order, order.getRider());

        return toResponse(order);
    }

    @Transactional
    public OrderResponse riderDeliver(String orderId, String riderEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getRider().getEmail().equals(riderEmail)) {
            throw new IllegalStateException("This rider is not assigned.");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        saveHistory(order, order.getRider());

        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String orderId, String restaurantEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!order.getRestaurant().getEmail().equals(restaurantEmail)) {
            throw new IllegalStateException("This restaurant does not own the order.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be canceled.");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        saveHistory(order, order.getRestaurant());

        return toResponse(order);
    }

    public List<OrderStatusHistoryResponse> getHistory(String orderId) {
        return historyRepository.findByOrderOrderByCreatedAtAsc(orderId)
                .stream()
                .map(h -> new OrderStatusHistoryResponse(
                        h.getStatus().name(),
                        h.getChangedByUser().getName(),
                        h.getCreatedAt()))
                .toList();
    }

    // ====Responses==========
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

    private void saveHistory(Order order, User changedBy) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.setOrder(order);
        h.setStatus(order.getStatus());
        h.setChangedByUser(changedBy);
        h.setCreatedAt(LocalDateTime.now());
        historyRepository.save(h);
    }

}
