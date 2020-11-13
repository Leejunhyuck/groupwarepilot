package org.raccoon.project.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.raccoon.project.chat.application.ChatService;
import org.raccoon.project.chat.model.ChatMessage;
import org.raccoon.project.chat.model.ChatRoom;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebSocketChatHandler extends TextWebSocketHandler{
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    public WebSocketChatHandler(ObjectMapper objectMapper, ChatService chatService){
        this.objectMapper = objectMapper;
        this.chatService = chatService;
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId());
        room.handleActions(session, chatMessage, chatService);
    }
    
}
