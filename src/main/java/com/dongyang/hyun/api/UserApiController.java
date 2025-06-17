package com.dongyang.hyun.api;

import com.dongyang.hyun.dto.UserDto;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApiController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<User> signup(@RequestBody UserDto dto) {
        User user = userService.signup(dto);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.badRequest().build();
    }

    @PostMapping("/api/login")
    public ResponseEntity<User> login(@RequestBody UserDto dto, HttpSession session) {
        User user = userService.login(dto);
        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/api/mypage")
    public ResponseEntity<User> mypage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/api/mypage/update")
    public ResponseEntity<User> updateProfile(@RequestBody UserDto dto, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User updated = userService.updateProfile(sessionUser.getId(), dto);
        session.setAttribute("user", updated);
        return ResponseEntity.ok(updated);
    }



}
