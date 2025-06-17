package com.dongyang.hyun.api;

import com.dongyang.hyun.entity.FriendRequest;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.service.FriendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendApiController {
    @Autowired
    private FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(@RequestParam Long toUserId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        boolean ok = friendService.sendRequest(user.getId(), toUserId);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<?> accept(@RequestParam Long requestId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        boolean ok = friendService.acceptRequest(requestId, user.getId());
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<?> reject(@RequestParam Long requestId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        boolean ok = friendService.rejectRequest(requestId, user.getId());
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public List<User> friends(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return List.of();
        return friendService.getFriends(user.getId());
    }

    @GetMapping("/requests")
    public List<FriendRequest> pendingRequests(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return List.of();
        return friendService.getPendingRequests(user.getId());
    }
}
