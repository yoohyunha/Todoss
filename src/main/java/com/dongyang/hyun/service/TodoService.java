package com.dongyang.hyun.service;

import com.dongyang.hyun.dto.TodoDto;
import com.dongyang.hyun.entity.Todo;
import com.dongyang.hyun.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public List<Todo> findByUserId(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    // REST API용 (DTO 기반)
    public Todo create(TodoDto dto) {
        Todo todo = dto.toEntity();
        return create(todo); // 아래 엔티티 기반 메서드 재사용
    }

    public Todo create(Todo todo) {
        if (todo.getId() != null) return null;
        return todoRepository.save(todo);
    }
    public Todo update(Long id, TodoDto dto) {
        Todo target = todoRepository.findById(id).orElse(null);
        if (target == null) return null;
        // completed만 갱신
        target.setCompleted(dto.isCompleted());
        return todoRepository.save(target);
    }

    public boolean delete(Long id) {
        if (!todoRepository.existsById(id)) return false;
        todoRepository.deleteById(id);
        return true;
    }

    public List<Todo> findByUserIdAndDate(Long userId, LocalDate date) {
        return todoRepository.findByUserIdAndDate(userId, date);
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id).orElse(null);
    }
    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }
    public List<Todo> findByUserIdAndYear(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end   = LocalDate.of(year, 12, 31);
        return todoRepository.findByUserIdAndDateBetween(userId, start, end);
    }


}
