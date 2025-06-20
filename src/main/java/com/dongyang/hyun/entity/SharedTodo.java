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
public class SharedTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy; // 작성자

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo; // 담당자 (null이면 공통 할일)

    @ManyToOne
    @JoinColumn(name = "shared_list_id")
    private SharedTodoList sharedList;
}
