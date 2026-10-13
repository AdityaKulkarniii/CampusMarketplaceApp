package edu.campusmarketplace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketplace_notifications")
public class MarketplaceNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private MarketplaceUser recipient;
    @Column(nullable = false, length = 1000)
    private String message;
    @Column(nullable = false)
    private boolean isRead = false;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected MarketplaceNotification() { }
    public MarketplaceNotification(MarketplaceUser recipient, String message) { this.recipient = recipient; this.message = message; }
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public MarketplaceUser getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}