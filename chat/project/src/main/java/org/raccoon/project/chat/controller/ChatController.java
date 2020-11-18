package org.raccoon.project.chat.controller;

import org.raccoon.project.chat.model.ChatMessage;
import org.raccoon.project.chat.repository.ChatRoomRepository;
import org.raccoon.project.chat.service.JwtTokenProvider;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;



@Controller
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChannelTopic channelTopic;

    public ChatController(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, JwtTokenProvider jwtTokenProvider){
        this.redisTemplate = redisTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
        this.channelTopic = channelTopic;
    }
    
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
        String nickname = jwtTokenProvider.getUserNameFromJwt(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(nickname);
        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

}
