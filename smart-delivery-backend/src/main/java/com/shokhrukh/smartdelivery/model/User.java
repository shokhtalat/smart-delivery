package com.shokhrukh.smartdelivery.model;

import com.shokhrukh.smartdelivery.enums.Role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;
}