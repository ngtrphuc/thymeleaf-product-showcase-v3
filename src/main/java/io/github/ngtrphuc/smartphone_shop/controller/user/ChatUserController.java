package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.github.ngtrphuc.smartphone_shop.service.ChatService;

@RestController
public class ChatUserController {

    private final ChatService chatService;

    public ChatUserController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/history")
    public List<Map<String, Object>> chatHistory(Authentication auth) {
        String email = auth.getName();
        chatService.markReadByUser(email);
        return chatService.getHistory(email).stream().map(m -> {
            Map<String, Object> item = new HashMap<>();
            item.put("content", m.getContent());
            item.put("senderRole", m.getSenderRole());
            item.put("createdAt", m.getCreatedAt().toString());
            return item;
        }).collect(Collectors.toList());
    }

    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public SseEmitter streamUser(Authentication auth) {
        return chatService.subscribeUser(auth.getName());
    }

    @PostMapping("/chat/send")
    public String sendUserMessage(Authentication auth, @RequestParam String content) {
        if (content == null || content.isBlank()) return "error";
        chatService.saveUserMessage(auth.getName(), content.trim());
        return "ok";
    }

    @GetMapping("/chat/unread-count")
    public long userUnreadCount(Authentication auth) {
        return chatService.countUnreadByUser(auth.getName());
    }

    @PostMapping("/chat/mark-read")
    public String markRead(Authentication auth) {
        chatService.markReadByUser(auth.getName());
        return "ok";
    }
}
