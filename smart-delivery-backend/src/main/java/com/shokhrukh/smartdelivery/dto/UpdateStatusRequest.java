package com.shokhrukh.smartdelivery.dto;

import com.shokhrukh.smartdelivery.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private OrderStatus status;
}
