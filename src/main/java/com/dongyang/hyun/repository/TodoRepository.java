package com.dongyang.hyun.repository;

import com.dongyang.hyun.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserId(Long userId);

    List<Todo> findByUserIdAndDate(Long userId, LocalDate date);

    List<Todo> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

}