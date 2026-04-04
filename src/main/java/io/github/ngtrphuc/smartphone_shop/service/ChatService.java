package io.github.ngtrphuc.smartphone_shop.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.github.ngtrphuc.smartphone_shop.model.ChatMessage;
import io.github.ngtrphuc.smartphone_shop.repository.ChatMessageRepository;
import jakarta.transaction.Transactional;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final List<SseEmitter> adminEmitters = new CopyOnWriteArrayList<>();

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public SseEmitter subscribeUser(String email) {
        SseEmitter emitter = new SseEmitter(300_000L);
        userEmitters.computeIfAbsent(email, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeUserEmitter(email, emitter));
        emitter.onTimeout(() -> removeUserEmitter(email, emitter));
        emitter.onError(e -> removeUserEmitter(email, emitter));
        return emitter;
    }

    public SseEmitter subscribeAdmin() {
        SseEmitter emitter = new SseEmitter(300_000L);
        adminEmitters.add(emitter);
        emitter.onCompletion(() -> adminEmitters.remove(emitter));
        emitter.onTimeout(() -> adminEmitters.remove(emitter));
        emitter.onError(e -> adminEmitters.remove(emitter));
        return emitter;
    }

    @Transactional
    public ChatMessage saveUserMessage(String email, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setUserEmail(email);
        msg.setContent(content);
        msg.setSenderRole("USER");
        msg.setReadByAdmin(false);
        msg.setReadByUser(true);
        ChatMessage saved = chatMessageRepository.save(msg);
        pushToAdmins(email, saved);
        return saved;
    }

    @Transactional
    public ChatMessage saveAdminMessage(String userEmail, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setUserEmail(userEmail);
        msg.setContent(content);
        msg.setSenderRole("ADMIN");
        msg.setReadByAdmin(true);
        msg.setReadByUser(false);
        ChatMessage saved = chatMessageRepository.save(msg);
        pushToUser(userEmail, saved);
        pushToAdmins(userEmail, saved);
        return saved;
    }

    public List<ChatMessage> getHistory(String email) {
        return chatMessageRepository.findByUserEmailOrderByCreatedAtAsc(email);
    }

    public List<String> getAllConversationEmails() {
        return chatMessageRepository.findDistinctUserEmailsOrderByLatest();
    }

    public long countUnreadByAdmin(String email) {
        return chatMessageRepository.countUnreadByAdmin(email);
    }

    public long countUnreadByUser(String email) {
        return chatMessageRepository.countUnreadByUser(email);
    }

    public long countAllUnreadByAdmin() {
        return chatMessageRepository.countAllUnreadByAdmin();
    }

    @Transactional
    public void markReadByAdmin(String email) {
        chatMessageRepository.markAllReadByAdmin(email);
    }

    @Transactional
    public void markReadByUser(String email) {
        chatMessageRepository.markAllReadByUser(email);
    }

    private void pushToAdmins(String conversationEmail, ChatMessage msg) {
        String payload = Objects.requireNonNull(buildPayload(msg, conversationEmail));
        List<SseEmitter> dead = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : adminEmitters) {
            try {
                emitter.send(SseEmitter.event().name("message").data(payload));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        adminEmitters.removeAll(dead);
    }

    private void pushToUser(String email, ChatMessage msg) {
        List<SseEmitter> emitters = userEmitters.get(email);
        if (emitters == null) {
            return;
        }
        String payload = Objects.requireNonNull(buildPayload(msg, email));
        List<SseEmitter> dead = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("message").data(payload));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }

    private void removeUserEmitter(String email, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(email);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    private String buildPayload(ChatMessage msg, String conversationEmail) {
        return "{\"id\":" + msg.getId()
                + ",\"userEmail\":\"" + escapeJson(conversationEmail) + "\""
                + ",\"content\":\"" + escapeJson(msg.getContent()) + "\""
                + ",\"senderRole\":\"" + msg.getSenderRole() + "\""
                + ",\"createdAt\":\"" + msg.getCreatedAt() + "\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
