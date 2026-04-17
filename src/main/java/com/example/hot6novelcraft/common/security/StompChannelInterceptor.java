package com.example.hot6novelcraft.common.security;

import com.example.hot6novelcraft.common.exception.ServiceErrorException;
import com.example.hot6novelcraft.common.exception.domain.ChatExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.hot6novelcraft.domain.user.entity.UserDetailsImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // CONNECT 프레임에서만 JWT 검증
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith(JwtUtil.BEARER_PREFIX)) {
                throw new ServiceErrorException(ChatExceptionEnum.ERR_WEBSOCKET_UNAUTHORIZED);
            }

            String token = jwtUtil.substringToken(authHeader);
            if (!jwtUtil.validateToken(token)) {
                throw new ServiceErrorException(ChatExceptionEnum.ERR_WEBSOCKET_UNAUTHORIZED);
            }

            String email = jwtUtil.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Long userId = ((UserDetailsImpl) userDetails).getUser().getId();

            // userId를 Principal로 등록 → 컨트롤러에서 principal.getName()으로 접근
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    userId.toString(), null, userDetails.getAuthorities()
            ));
            log.info("[WebSocket] 연결 인증 완료 userId={}", userId);
        }

        return message;
    }
}
