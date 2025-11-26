package com.shokhrukh.smartdelivery.dto;

import com.shokhrukh.smartdelivery.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String customerName;
    private String customerPhone;
    private String dropoffAddress;
    private String notes;
    private OrderStatus status;
    private String restaurantName;
    private String riderName;
}
