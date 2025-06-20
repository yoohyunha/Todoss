package com.dongyang.hyun.controller;

import com.dongyang.hyun.dto.TodoDto;
import com.dongyang.hyun.entity.FriendRequest;
import com.dongyang.hyun.entity.SharedTodoList;
import com.dongyang.hyun.entity.Todo;
import com.dongyang.hyun.entity.User;
import com.dongyang.hyun.service.FriendService;
import com.dongyang.hyun.service.SharedTodoService;
import com.dongyang.hyun.service.TodoService;
import com.dongyang.hyun.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class TodoViewController {
    private final TodoService todoService;
    private final FriendService friendService;
    private final UserService userService;
    private final SharedTodoService sharedTodoService;

    public TodoViewController(TodoService todoService, FriendService friendService, UserService userService, SharedTodoService sharedTodoService) {
        this.todoService = todoService;
        this.friendService = friendService;
        this.userService = userService;
        this.sharedTodoService = sharedTodoService;
    }

    @PostMapping("/todos/create")
    public String createTodo(TodoDto dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Todo todo = dto.toEntity();

        todo.setUser(user);
        todoService.create(todo);
        return "redirect:/todos?date=" + todo.getDate(); // 추가 후 해당 날짜로 이동
    }


    @GetMapping("/todos")
    public String todosPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (date == null) date = LocalDate.now();

        // 기존 개인 투두 리스트
        List<Todo> todos = todoService.findByUserIdAndDate(user.getId(), date);

        // 친구 목록, 친구 요청 추가
        List<User> friends = friendService.getFriends(user.getId());
        List<FriendRequest> pendingRequests = friendService.getPendingRequests(user.getId());

        // 공유 투두 리스트 추가
        List<SharedTodoList> sharedLists = sharedTodoService.getSharedLists(user.getId());

        model.addAttribute("todos", todos);
        model.addAttribute("doneCount", todos.stream().filter(Todo::isCompleted).count());
        model.addAttribute("totalCount", todos.size());
        model.addAttribute("selectedDate", date);

        model.addAttribute("currentYear", date.getYear());
        model.addAttribute("currentMonth", date.getMonthValue());
        model.addAttribute("prevMonth", date.minusMonths(1).withDayOfMonth(1));
        model.addAttribute("nextMonth", date.plusMonths(1).withDayOfMonth(1));
        model.addAttribute("calendarRows", generateCalendarRows(date, date));

        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("sharedLists", sharedLists); // 공유 리스트 추가
        model.addAttribute("user", user);
        return "todos/todos";
    }

    @GetMapping("/todos/friend")
    public String friendTodos(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model, HttpSession session) {

        User me = (User) session.getAttribute("user");
        if (me == null) {
            return "redirect:/login";
        }
        if (userId == null ||
                (!me.getId().equals(userId) && !friendService.isFriend(me.getId(), userId))) {
            return "redirect:/todos";
        }

        User friend = userService.findById(userId);
        if (friend == null) {
            return "redirect:/todos";
        }

        LocalDate viewDate = (date != null) ? date : LocalDate.now();
        List<Todo> todos = todoService.findByUserIdAndDate(userId, viewDate);
        long doneCount = todos.stream().filter(Todo::isCompleted).count();
        int totalCount = todos.size();
        LocalDate prevMonth = viewDate.minusMonths(1).withDayOfMonth(1);
        LocalDate nextMonth = viewDate.plusMonths(1).withDayOfMonth(1);
        List<List<Map<String, Object>>> calendarRows = generateCalendarRows(viewDate, viewDate);


        model.addAttribute("user", me);
        model.addAttribute("friend", friend);
        model.addAttribute("todos", todos);
        model.addAttribute("doneCount", doneCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("selectedDate", viewDate);
        model.addAttribute("currentYear", viewDate.getYear());
        model.addAttribute("currentMonth", viewDate.getMonthValue());
        model.addAttribute("prevMonth", prevMonth);
        model.addAttribute("nextMonth", nextMonth);
        model.addAttribute("calendarRows", calendarRows);

        return "todos/friend-todos";
    }

    // 친구 연간 투두 리스트 뷰
    @GetMapping("/todos/friend/year")
    public String friendTodosYear(
            @RequestParam Long userId,
            @RequestParam int year,
            Model model, HttpSession session) {
        User me = (User) session.getAttribute("user");
        if (me == null || !friendService.isFriend(me.getId(), userId)) {
            return "redirect:/todos";
        }
        User friend = userService.findById(userId);
        List<Todo> todos = todoService.findByUserIdAndYear(userId, year);


        model.addAttribute("user", me);
        model.addAttribute("friend", friend);
        model.addAttribute("todos", todos);
        model.addAttribute("year", year);
        return "todos/friend-todos";
    }





    private List<List<Map<String, Object>>> generateCalendarRows(LocalDate baseDate, LocalDate selectedDate) {
        YearMonth ym = YearMonth.from(baseDate);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        List<List<Map<String, Object>>> calendarRows = new ArrayList<>();
        List<Map<String, Object>> week = new ArrayList<>();

        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday=0
        for (int i = 0; i < dayOfWeek; i++) {
            week.add(Map.of("day", "", "date", ""));
        }
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            week.add(Map.of(
                    "day", date.getDayOfMonth(),
                    "date", date.toString(),
                    "today", date.equals(LocalDate.now()),
                    "isSelected", date.equals(selectedDate)
            ));
            if (week.size() == 7) {
                calendarRows.add(week);
                week = new ArrayList<>();
            }
        }
        while (week.size() < 7) {
            week.add(Map.of("day", "", "date", ""));
        }
        if (!week.isEmpty()) calendarRows.add(week);
        return calendarRows;
    }



}
