package com.shokhrukh.smartdelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shokhrukh.smartdelivery.enums.Role;
import com.shokhrukh.smartdelivery.entity.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

}
