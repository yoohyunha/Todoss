package com.dongyang.hyun.service;

import com.dongyang.hyun.dto.SharedTodoDto;
import com.dongyang.hyun.entity.SharedTodo;
import com.dongyang.hyun.entity.SharedTodoList;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.repository.SharedTodoListRepository;
import com.dongyang.hyun.repository.SharedTodoRepository;
import com.dongyang.hyun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SharedTodoService {
    @Autowired
    private SharedTodoRepository sharedTodoRepository;
    @Autowired
    private SharedTodoListRepository sharedTodoListRepository;
    @Autowired
    private UserRepository userRepository;

    public List<SharedTodoList> getSharedLists(Long userId) {
        return sharedTodoListRepository.findByUserId(userId);
    }

    public SharedTodoList createSharedList(String title, Long createdById, Long sharedWithId) {
        User creator = userRepository.findById(createdById).orElse(null);
        User sharedWith = userRepository.findById(sharedWithId).orElse(null);
        if (creator == null || sharedWith == null) return null;

        SharedTodoList sharedList = new SharedTodoList();
        sharedList.setTitle(title);
        sharedList.setCreatedBy(creator);
        sharedList.setSharedWith(sharedWith);
        return sharedTodoListRepository.save(sharedList);
    }

    public List<SharedTodo> getSharedTodos(Long sharedListId, LocalDate date) {
        return sharedTodoRepository.findBySharedListIdAndDate(sharedListId, date);
    }

    public SharedTodo createSharedTodo(SharedTodoDto dto, Long createdById) {
        User creator = userRepository.findById(createdById).orElse(null);
        SharedTodoList sharedList = sharedTodoListRepository.findById(dto.getSharedListId()).orElse(null);
        if (creator == null || sharedList == null) return null;

        User assignedTo = null;
        if (dto.getAssignedToId() != null) {
            assignedTo = userRepository.findById(dto.getAssignedToId()).orElse(null);
        }

        SharedTodo sharedTodo = new SharedTodo();
        sharedTodo.setContent(dto.getContent());
        sharedTodo.setDate(LocalDate.parse(dto.getDate()));
        sharedTodo.setCreatedBy(creator);
        sharedTodo.setAssignedTo(assignedTo);
        sharedTodo.setSharedList(sharedList);
        return sharedTodoRepository.save(sharedTodo);
    }

    public SharedTodo toggleSharedTodo(Long id) {
        SharedTodo todo = sharedTodoRepository.findById(id).orElse(null);
        if (todo == null) return null;
        todo.setCompleted(!todo.isCompleted());
        return sharedTodoRepository.save(todo);
    }

    // 공유 투두 삭제 기능 추가
    public boolean deleteSharedTodo(Long id, Long userId) {
        SharedTodo todo = sharedTodoRepository.findById(id).orElse(null);
        if (todo == null) return false;

        // 작성자이거나 공유 리스트의 멤버인지 확인
        if (!hasAccess(userId, todo.getSharedList().getId())) return false;

        sharedTodoRepository.delete(todo);
        return true;
    }

    // 공유 리스트 삭제 기능 추가
    public boolean deleteSharedList(Long sharedListId, Long userId) {
        SharedTodoList sharedList = sharedTodoListRepository.findById(sharedListId).orElse(null);
        if (sharedList == null) return false;

        // 생성자만 삭제 가능
        if (!sharedList.getCreatedBy().getId().equals(userId)) return false;

        sharedTodoListRepository.delete(sharedList);
        return true;
    }

    public boolean hasAccess(Long userId, Long sharedListId) {
        SharedTodoList sharedList = sharedTodoListRepository.findById(sharedListId).orElse(null);
        if (sharedList == null) return false;
        return sharedList.getCreatedBy().getId().equals(userId) ||
                sharedList.getSharedWith().getId().equals(userId);
    }
}
