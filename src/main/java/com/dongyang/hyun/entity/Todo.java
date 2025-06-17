package com.dongyang.hyun.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private LocalDate date; // 날짜별 할 일 관리


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // User 없이 생성하는 생성자 추가
    public Todo(Long id, String content, boolean completed) {
        this.id = id;
        this.content = content;
        this.completed = completed;
        this.user = null; // 필요시 null 또는 나중에 setUser로 할당
    }
}
