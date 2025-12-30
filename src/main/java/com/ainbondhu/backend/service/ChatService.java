package com.ainbondhu.backend.service;

import com.ainbondhu.backend.domain.entity.ChatMessage;
import com.ainbondhu.backend.dto.ChatMessageDto;
import com.ainbondhu.backend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessageDto chatMessageDto) {
        ChatMessage message = new ChatMessage();
        message.setSenderId(chatMessageDto.getSenderId());
        message.setReceiverId(chatMessageDto.getReceiverId());
        message.setContent(chatMessageDto.getContent());
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessageDto> getChatHistory(String senderId, String receiverId) {
        List<ChatMessage> sent = chatMessageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        List<ChatMessage> received = chatMessageRepository.findBySenderIdAndReceiverId(receiverId, senderId);

        List<ChatMessage> all = new ArrayList<>();
        all.addAll(sent);
        all.addAll(received);

        return all.stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .map(msg -> ChatMessageDto.builder()
                        .senderId(msg.getSenderId())
                        .receiverId(msg.getReceiverId())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp().toString())
                        .build())
                .collect(Collectors.toList());
    }
}
