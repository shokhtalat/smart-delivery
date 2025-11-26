package com.shokhrukh.smartdelivery.dto;

import com.shokhrukh.smartdelivery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String email;
    private String name;
    private Role role;
}