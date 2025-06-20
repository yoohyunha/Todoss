package com.dongyang.hyun.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedTodoDto {
    private Long id;
    private String content;
    private boolean completed;
    private String date;
    private Long assignedToId; // 담당자 ID (null이면 공통 할일)
    private Long sharedListId;
}
