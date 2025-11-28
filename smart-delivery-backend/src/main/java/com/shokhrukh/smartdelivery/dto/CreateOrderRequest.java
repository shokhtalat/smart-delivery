package com.shokhrukh.smartdelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    @NotBlank
    private String dropoffAddress;

    private String notes; // optional

}
