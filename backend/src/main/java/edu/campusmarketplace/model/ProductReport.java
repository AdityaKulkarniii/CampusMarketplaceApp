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
@Table(name = "product_reports")
public class ProductReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private MarketplaceUser reporter;
    @Column(nullable = false, length = 500)
    private String reason;
    @Column(nullable = false)
    private boolean resolved = false;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ProductReport() { }
    public ProductReport(Product product, MarketplaceUser reporter, String reason) { this.product = product; this.reporter = reporter; this.reason = reason; }
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public MarketplaceUser getReporter() { return reporter; }
    public String getReason() { return reason; }
    public boolean isResolved() { return resolved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void resolve() { resolved = true; }
}