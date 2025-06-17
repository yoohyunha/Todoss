package com.dongyang.hyun.dto;

import com.dongyang.hyun.entity.Todo;
import com.dongyang.hyun.entity.User;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TodoDto {
    private Long id;
    private String content;
    private boolean completed;
    private LocalDate date; // 추가
    public Todo toEntity() {
        return new Todo(id, content, completed, date, null);
    }
}
