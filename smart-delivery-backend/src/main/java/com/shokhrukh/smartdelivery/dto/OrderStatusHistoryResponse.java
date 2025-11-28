package com.shokhrukh.smartdelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderStatusHistoryResponse {
    private String status;
    private String changedBy;
    private LocalDateTime createdAt;
}