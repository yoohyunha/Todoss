package com.dongyang.hyun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne @JoinColumn(name = "to_user_id")
    private User toUser;

    @Enumerated(EnumType.STRING)
    private Status status; // PENDING, ACCEPTED, REJECTED

    public enum Status { PENDING, ACCEPTED, REJECTED }
}
