package com.ainbondhu.backend.controller;

import com.ainbondhu.backend.dto.ChatMessageDto;
import com.ainbondhu.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDto chatMessage) {
        chatService.saveMessage(chatMessage);

        // Send to receiver's specific queue
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverId(), "/queue/messages",
                chatMessage
        );
    }

    @GetMapping("/api/v1/chat/history")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        return ResponseEntity.ok(chatService.getChatHistory(userId1, userId2));
    }
}
