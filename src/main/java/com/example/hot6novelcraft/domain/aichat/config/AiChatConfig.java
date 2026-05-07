package com.example.hot6novelcraft.domain.aichat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiChatConfig {

    /**
     * 대화 메모리 빈
     * - MessageWindowChatMemory: 최근 N턴만 유지해서 토큰 비용 절감
     * - maxMessages(10): 사용자/AI 메시지 합산 최대 10개 유지
     * - 개발: InMemoryChatMemoryRepository (기본값)
     * - 운영: 4단계에서 Redis 기반으로 교체 예정
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient customerServiceChatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4.1-nano")
                        .build())
                .defaultAdvisors(
                        new SimpleLoggerAdvisor() // 디버깅용 로그
                        // MessageChatMemoryAdvisor는 conversationId가 요청마다 다르므로 서비스에서 요청별 생성
                )
                .defaultSystem("""
                        너는 NovelCraft 고객센터 AI다.
                        다음 규칙을 반드시 지켜라:
                        1. 반드시 제공된 문서 기반으로만 답변한다.
                        2. 문서에 없으면 "담당자에게 문의해주세요"라고 말한다.
                        3. 답변은 친절하고 짧게 (3~5줄)
                        4. 불필요한 설명 금지
                        5. 한국어로만 답변
                        """)
                .build();
    }
}
