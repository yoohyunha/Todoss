package com.dongyang.hyun.repository;

import com.dongyang.hyun.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByUsernameContainingAndIdNot(String username, Long id);
}