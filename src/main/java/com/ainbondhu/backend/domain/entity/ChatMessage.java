package com.ainbondhu.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId; // User UUID as String
    private String receiverId; // User UUID as String

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;
}
