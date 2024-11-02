package com.spring.authentication.repository;

import com.spring.authentication.Modals.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email); // Using optional because we want to check whether user exists or not
}
