package com.example.hot6novelcraft.domain.chatroom.dto.request;

import com.example.hot6novelcraft.domain.chatmessage.entity.enums.MessageType;

public record ChatMessageRequest(String content, MessageType messageType) {
}
