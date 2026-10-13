package edu.campusmarketplace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "seller_ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "rater_id"}))
public class SellerRating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private MarketplaceUser seller;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rater_id", nullable = false)
    private MarketplaceUser rater;
    @Column(nullable = false)
    private int score;
    protected SellerRating() { }
    public SellerRating(Product product, MarketplaceUser seller, MarketplaceUser rater, int score) {
        this.product = product; this.seller = seller; this.rater = rater; this.score = score;
    }
}