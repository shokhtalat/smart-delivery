package com.shokhrukh.smartdelivery.dto;

import com.shokhrukh.smartdelivery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String name;
    private String phone;
    private Role role;
}