package edu.campusmarketplace.repository;

import edu.campusmarketplace.model.MarketplaceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<MarketplaceNotification, Long> {
    List<MarketplaceNotification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
}