package com.example.hot6novelcraft.domain.chatroom.controller;

import com.example.hot6novelcraft.domain.chatroom.dto.request.ChatMessageRequest;
import com.example.hot6novelcraft.domain.chatroom.dto.response.ChatEventResponse;
import com.example.hot6novelcraft.domain.chatroom.dto.response.ChatMessageResponse;
import com.example.hot6novelcraft.domain.chatroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     * - 클라이언트: STOMP SEND /app/chat/{roomId}
     * - 브로드캐스트: /topic/chat/{roomId}
     * - principal.getName() = StompChannelInterceptor에서 설정한 userId
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequest request,
            Principal principal) {
        Long senderId = Long.parseLong(principal.getName());

        ChatMessageResponse response = chatService.saveMessage(roomId, senderId, request);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, ChatEventResponse.message(response));

        log.info("[WebSocket] 메시지 전송 roomId={} senderId={}", roomId, senderId);
    }
}
