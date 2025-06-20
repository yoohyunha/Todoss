package com.dongyang.hyun.repository;

import com.dongyang.hyun.entity.SharedTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SharedTodoRepository extends JpaRepository<SharedTodo, Long> {
    List<SharedTodo> findBySharedListIdAndDate(Long sharedListId, LocalDate date);
    List<SharedTodo> findBySharedListId(Long sharedListId);
}
