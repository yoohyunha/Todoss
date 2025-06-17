package com.dongyang.hyun.controller;

import com.dongyang.hyun.dto.UserDto;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.service.FriendService;
import com.dongyang.hyun.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserViewController {
    private final UserService userService;
    private final FriendService friendService;

    public UserViewController(UserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }

    @GetMapping("/login")
    public String loginPage() { return "user/login"; }

    @PostMapping("/login")
    public String login(UserDto dto, HttpSession session, Model model) {
        User user = userService.login(dto);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/todos";
        }
        model.addAttribute("loginError", true);
        return "user/login";
    }

    @GetMapping("/signup")
    public String signupPage() { return "user/signup"; }

    @PostMapping("/signup")
    public String signup(UserDto dto, Model model) {
        User user = userService.signup(dto);
        if (user != null) {
            return "redirect:/login";
        }
        model.addAttribute("signupError", true);
        return "user/signup";
    }
    @GetMapping("/mypage")
    public String mypage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "user/mypage";
    }

    @PostMapping("/mypage/update")
    public String updateProfile(UserDto dto, HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";
        User updated = userService.updateProfile(sessionUser.getId(), dto);
        session.setAttribute("user", updated);
        model.addAttribute("user", updated);
        model.addAttribute("updateSuccess", true);
        return "user/mypage";
    }

    @GetMapping("/friends")
    public String friendsPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        // 검색 결과/친구 목록 등도 필요시 추가
        return "user/friends";
    }

    @PostMapping("/friends/search")
    public String searchFriends(@RequestParam String keyword, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        // UserRepository에 findByUsernameContaining 등으로 검색
        List<User> results = userService.searchByUsername(keyword, user.getId());
        model.addAttribute("results", results);
        return "user/friends";
    }

    @PostMapping("/friends/request")
    public String requestFriend(@RequestParam Long toUserId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        friendService.sendRequest(user.getId(), toUserId);
        return "redirect:/friends";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    @PostMapping("/friends/accept")
    public String acceptFriend(@RequestParam Long requestId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        friendService.acceptRequest(requestId, user.getId());
        return "redirect:/todos";
    }

    @PostMapping("/friends/reject")
    public String rejectFriend(@RequestParam Long requestId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        friendService.rejectRequest(requestId, user.getId());
        return "redirect:/todos";
    }



}
