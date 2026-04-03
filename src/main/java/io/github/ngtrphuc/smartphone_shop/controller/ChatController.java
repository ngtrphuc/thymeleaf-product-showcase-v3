package io.github.ngtrphuc.smartphone_shop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.github.ngtrphuc.smartphone_shop.service.ChatService;
import jakarta.transaction.Transactional;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/history")
    @ResponseBody
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
    @ResponseBody
    public SseEmitter streamUser(Authentication auth) {
        return chatService.subscribeUser(auth.getName());
    }

    @PostMapping("/chat/send")
    @ResponseBody
    public String sendUserMessage(Authentication auth, @RequestParam String content) {
        if (content == null || content.isBlank()) return "error";
        chatService.saveUserMessage(auth.getName(), content.trim());
        return "ok";
    }

    @GetMapping("/chat/unread-count")
    @ResponseBody
    public long userUnreadCount(Authentication auth) {
        return chatService.countUnreadByUser(auth.getName());
    }

    @PostMapping("/chat/mark-read")
    @ResponseBody
    public String markRead(Authentication auth) {
        chatService.markReadByUser(auth.getName());
        return "ok";
    }

    @GetMapping("/admin/chat")
    public String adminChatPage(Model model) {
        List<String> emails = chatService.getAllConversationEmails();
        Map<String, Long> unreadCounts = new HashMap<>();
        for (String email : emails) {
            unreadCounts.put(email, chatService.countUnreadByAdmin(email));
        }
        model.addAttribute("emails", emails);
        model.addAttribute("unreadCounts", unreadCounts);
        if (!emails.isEmpty()) {
            String first = emails.get(0);
            model.addAttribute("selectedEmail", first);
            model.addAttribute("history", chatService.getHistory(first));
            chatService.markReadByAdmin(first);
        }
        return "admin/chat";
    }

    @GetMapping("/admin/chat/{email}")
    public String adminChatConversation(@PathVariable String email, Model model) {
        List<String> emails = chatService.getAllConversationEmails();
        Map<String, Long> unreadCounts = new HashMap<>();
        for (String e : emails) {
            unreadCounts.put(e, chatService.countUnreadByAdmin(e));
        }
        chatService.markReadByAdmin(email);
        model.addAttribute("emails", emails);
        model.addAttribute("unreadCounts", unreadCounts);
        model.addAttribute("selectedEmail", email);
        model.addAttribute("history", chatService.getHistory(email));
        return "admin/chat";
    }

    @GetMapping(value = "/admin/chat/stream", produces = "text/event-stream")
    @ResponseBody
    public SseEmitter streamAdmin() {
        return chatService.subscribeAdmin();
    }

    @PostMapping("/admin/chat/send")
    @ResponseBody
    @Transactional
    public String sendAdminMessage(@RequestParam String userEmail, @RequestParam String content) {
        if (content == null || content.isBlank() || userEmail == null) return "error";
        chatService.saveAdminMessage(userEmail, content.trim());
        return "ok";
    }

    @GetMapping("/admin/chat/unread-count")
    @ResponseBody
    public long adminUnreadCount() {
        return chatService.countAllUnreadByAdmin();
    }
}