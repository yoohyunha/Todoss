package com.dongyang.hyun.api;

import com.dongyang.hyun.dto.SharedTodoDto;
import com.dongyang.hyun.entity.SharedTodo;
import com.dongyang.hyun.entity.SharedTodoList;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.service.SharedTodoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shared-todos")
public class SharedTodoApiController {
    @Autowired
    private SharedTodoService sharedTodoService;

    @GetMapping("/lists")
    public ResponseEntity<List<SharedTodoList>> getSharedLists(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(sharedTodoService.getSharedLists(user.getId()));
    }

    @PostMapping("/lists")
    public ResponseEntity<SharedTodoList> createSharedList(
            @RequestParam String title,
            @RequestParam Long sharedWithId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        SharedTodoList created = sharedTodoService.createSharedList(title, user.getId(), sharedWithId);
        return created != null ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
    }

    // 공유 리스트 삭제
    @DeleteMapping("/lists/{sharedListId}")
    public ResponseEntity<Void> deleteSharedList(@PathVariable Long sharedListId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        boolean deleted = sharedTodoService.deleteSharedList(sharedListId, user.getId());
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{sharedListId}")
    public ResponseEntity<List<SharedTodo>> getSharedTodos(
            @PathVariable Long sharedListId,
            @RequestParam String date,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!sharedTodoService.hasAccess(user.getId(), sharedListId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<SharedTodo> todos = sharedTodoService.getSharedTodos(sharedListId, LocalDate.parse(date));
        return ResponseEntity.ok(todos);
    }

    @PostMapping("")
    public ResponseEntity<SharedTodo> createSharedTodo(@RequestBody SharedTodoDto dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!sharedTodoService.hasAccess(user.getId(), dto.getSharedListId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SharedTodo created = sharedTodoService.createSharedTodo(dto, user.getId());
        return created != null ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SharedTodo> toggleSharedTodo(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        SharedTodo updated = sharedTodoService.toggleSharedTodo(id);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.badRequest().build();
    }

    // 공유 투두 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSharedTodo(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        boolean deleted = sharedTodoService.deleteSharedTodo(id, user.getId());
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
