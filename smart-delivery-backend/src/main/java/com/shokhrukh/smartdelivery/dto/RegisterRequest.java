package com.shokhrukh.smartdelivery.dto;

import com.shokhrukh.smartdelivery.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private Role role;
}