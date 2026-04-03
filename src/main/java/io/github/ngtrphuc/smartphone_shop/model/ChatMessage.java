package io.github.ngtrphuc.smartphone_shop.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private String senderRole;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean readByAdmin = false;

    @Column(nullable = false)
    private boolean readByUser = false;

    public ChatMessage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isReadByAdmin() { return readByAdmin; }
    public void setReadByAdmin(boolean readByAdmin) { this.readByAdmin = readByAdmin; }
    public boolean isReadByUser() { return readByUser; }
    public void setReadByUser(boolean readByUser) { this.readByUser = readByUser; }
}