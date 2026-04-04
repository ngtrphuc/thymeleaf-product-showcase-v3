package io.github.ngtrphuc.smartphone_shop.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ChatAdminController {

    private final ChatService chatService;

    public ChatAdminController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/admin/chat")
    public String adminChatPage(Model model) {
        List<String> emails = chatService.getAllConversationEmails();
        Map<String, Long> unreadCounts = new HashMap<>();
        for (String email : emails) unreadCounts.put(email, chatService.countUnreadByAdmin(email));
        model.addAttribute("emails", emails);
        model.addAttribute("unreadCounts", unreadCounts);
        if (!emails.isEmpty()) {
            String first = emails.get(0);
            model.addAttribute("selectedEmail", first);
            model.addAttribute("history", chatService.getHistory(first));
            chatService.markReadByAdmin(first);
        }
        return "chat";
    }

    @GetMapping("/admin/chat/{email}")
    public String adminChatConversation(@PathVariable String email, Model model) {
        List<String> emails = chatService.getAllConversationEmails();
        Map<String, Long> unreadCounts = new HashMap<>();
        for (String e : emails) unreadCounts.put(e, chatService.countUnreadByAdmin(e));
        chatService.markReadByAdmin(email);
        model.addAttribute("emails", emails);
        model.addAttribute("unreadCounts", unreadCounts);
        model.addAttribute("selectedEmail", email);
        model.addAttribute("history", chatService.getHistory(email));
        return "chat";
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
