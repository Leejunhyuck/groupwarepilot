package org.raccoon.project.chat.controller;

import org.raccoon.project.chat.model.ChatMessage;
import org.raccoon.project.chat.pubsub.RedisPublisher;
import org.raccoon.project.chat.repository.ChatRoomRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;




@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    public ChatController(RedisPublisher redisPublisher, ChatRoomRepository chatRoomRepository){
        this.redisPublisher = redisPublisher;
        this.chatRoomRepository = chatRoomRepository;
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
    

}
