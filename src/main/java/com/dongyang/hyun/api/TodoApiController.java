package com.dongyang.hyun.api;

import com.dongyang.hyun.dto.TodoDto;
import com.dongyang.hyun.entity.Todo;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.service.FriendService;
import com.dongyang.hyun.service.TodoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {
    @Autowired
    private TodoService todoService;
    @Autowired
    private FriendService friendService;

    @GetMapping("")
    public List<Todo> getTodos() {
        return todoService.findAll();
    }

    @PostMapping("")
    public ResponseEntity<Todo> create(@RequestBody TodoDto dto) {
        Todo created = todoService.create(dto);
        return (created != null) ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody TodoDto dto) {
        Todo target = todoService.findById(id);
        if (target == null) return ResponseEntity.badRequest().build();
        // completed만 갱신
        target.setCompleted(dto.isCompleted());
        Todo updated = todoService.save(target);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return todoService.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
    @GetMapping("/friend/{userId}")
    public ResponseEntity<?> getFriendTodos(@PathVariable Long userId, HttpSession session) {
        User me = (User) session.getAttribute("user");
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!friendService.isFriend(me.getId(), userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<Todo> todos = todoService.findByUserId(userId);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/friend/{userId}/year/{year}")
    public ResponseEntity<List<Todo>> getFriendTodosYear(
            @PathVariable Long userId,
            @PathVariable int year,
            HttpSession session) {
        User me = (User) session.getAttribute("user");
        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!friendService.isFriend(me.getId(), userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Todo> todos = todoService.findByUserIdAndYear(userId, year);
        return ResponseEntity.ok(todos);
    }


}
