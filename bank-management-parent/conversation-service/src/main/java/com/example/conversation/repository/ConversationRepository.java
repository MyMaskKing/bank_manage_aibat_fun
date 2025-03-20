package com.example.conversation.repository;

import com.example.conversation.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findBySessionIdOrderByTimestampAsc(String sessionId);
    List<Conversation> findByUserIdOrderByTimestampDesc(String userId);
} 