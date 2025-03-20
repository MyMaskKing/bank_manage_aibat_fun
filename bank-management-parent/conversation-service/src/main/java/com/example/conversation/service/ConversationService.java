package com.example.conversation.service;

import com.example.conversation.model.Conversation;
import com.example.conversation.repository.ConversationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Value("${conversation.record.path:./conversations}")
    private String recordPath;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Transactional
    public Conversation saveConversation(String userId, String message, String role) {
        String sessionId = UUID.randomUUID().toString();
        String markdownPath = generateMarkdownPath(userId, sessionId);
        
        Conversation conversation = new Conversation();
        conversation.setSessionId(sessionId);
        conversation.setUserId(userId);
        conversation.setMessage(message);
        conversation.setRole(role);
        conversation.setMarkdownPath(markdownPath);

        Conversation saved = conversationRepository.save(conversation);
        saveToMarkdown(saved);
        return saved;
    }

    private String generateMarkdownPath(String userId, String sessionId) {
        String timestamp = DATE_FORMATTER.format(java.time.LocalDateTime.now());
        return String.format("%s/%s_%s_%s.md", recordPath, userId, sessionId, timestamp);
    }

    private void saveToMarkdown(Conversation conversation) {
        try {
            Path filePath = Paths.get(conversation.getMarkdownPath());
            Files.createDirectories(filePath.getParent());

            String markdownContent = String.format("# 对话记录\n\n" +
                    "## 基本信息\n" +
                    "- 用户ID: %s\n" +
                    "- 会话ID: %s\n" +
                    "- 时间: %s\n\n" +
                    "## 对话内容\n\n" +
                    "### %s\n" +
                    "%s\n",
                    conversation.getUserId(),
                    conversation.getSessionId(),
                    conversation.getTimestamp(),
                    conversation.getRole().equals("user") ? "用户" : "助手",
                    conversation.getMessage());

            Files.writeString(filePath, markdownContent);
            log.info("对话记录已保存到: {}", conversation.getMarkdownPath());
        } catch (IOException e) {
            log.error("保存对话记录到Markdown文件失败", e);
            throw new RuntimeException("保存对话记录失败", e);
        }
    }

    public List<Conversation> getConversationsBySessionId(String sessionId) {
        return conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    public List<Conversation> getConversationsByUserId(String userId) {
        return conversationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * 保存对话记录到Markdown文件
     * @param userInput 用户输入
     * @param apiResult API执行结果
     * @return 是否保存成功
     */
    public boolean saveConversation(String userInput, String apiResult) {
        try {
            // 确保目录存在
            File dir = new File(recordPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("无法创建对话记录目录: {}", recordPath);
                    return false;
                }
            }
            
            // 生成文件名
            String fileName = "conversation_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                ".md";
            File file = new File(dir, fileName);
            
            // 构建Markdown内容
            StringBuilder markdown = new StringBuilder();
            if (!file.exists()) {
                markdown.append("# 对话记录\n\n");
                markdown.append("| 时间 | 用户输入 | API结果 |\n");
                markdown.append("| ---- | -------- | ------- |\n");
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            markdown.append("| ").append(timestamp).append(" | ")
                    .append(userInput).append(" | ")
                    .append(apiResult).append(" |\n");
            
            // 写入文件
            try (PrintWriter writer = new PrintWriter(
                    new FileWriter(file, StandardCharsets.UTF_8, true))) {
                writer.print(markdown.toString());
            }
            
            log.info("对话记录已保存到文件: {}", file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            log.error("保存对话记录失败", e);
            return false;
        }
    }
} 