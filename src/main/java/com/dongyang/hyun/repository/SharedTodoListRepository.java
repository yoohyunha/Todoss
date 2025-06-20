package com.dongyang.hyun.repository;

import com.dongyang.hyun.entity.SharedTodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedTodoListRepository extends JpaRepository<SharedTodoList, Long> {
    @Query("SELECT s FROM SharedTodoList s WHERE s.createdBy.id = :userId OR s.sharedWith.id = :userId")
    List<SharedTodoList> findByUserId(@Param("userId") Long userId);
}
