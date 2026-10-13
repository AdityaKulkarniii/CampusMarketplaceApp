package edu.campusmarketplace.model;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, length = 30)
    private String category;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false, length = 10, columnDefinition = "varchar(10) default 'SALE'")
    private String listingType = "SALE";
    @Column(length = 10)
    private String rentalPeriod;
    private Integer minimumRentalDuration;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(length = 1000)
    private String imageUrl;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_additional_images", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "image_order")
    @Column(name = "image_url", nullable = false, length = 1000)
    private List<String> additionalImageUrls = new ArrayList<>();
    @Column(nullable = false, length = 180)
    private String pickupLocation;
    @Column(nullable = false, length = 16)
    private String status = "AVAILABLE";
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private MarketplaceUser seller;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Product() { }
    public Product(String title, String category, BigDecimal price, String description, String imageUrl, String pickupLocation, MarketplaceUser seller) {
        this(title, category, price, "SALE", null, null, description, imageUrl, pickupLocation, seller);
    }
    public Product(String title, String category, BigDecimal price, String listingType, String rentalPeriod, Integer minimumRentalDuration,
                   String description, String imageUrl, String pickupLocation, MarketplaceUser seller) {
        this(title, category, price, listingType, rentalPeriod, minimumRentalDuration, description, imageUrl, List.of(), pickupLocation, seller);
    }
    public Product(String title, String category, BigDecimal price, String listingType, String rentalPeriod, Integer minimumRentalDuration,
                   String description, String imageUrl, List<String> additionalImageUrls, String pickupLocation, MarketplaceUser seller) {
        this.title = title; this.category = category; this.price = price; this.description = description;
        this.listingType = listingType; this.rentalPeriod = rentalPeriod; this.minimumRentalDuration = minimumRentalDuration;
        this.imageUrl = imageUrl; this.additionalImageUrls = additionalImageUrls == null ? new ArrayList<>() : new ArrayList<>(additionalImageUrls);
        this.pickupLocation = pickupLocation; this.seller = seller;
    }
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public String getListingType() { return listingType; }
    public String getRentalPeriod() { return rentalPeriod; }
    public Integer getMinimumRentalDuration() { return minimumRentalDuration; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getImageUrls() {
        List<String> images = new ArrayList<>();
        if (imageUrl != null && !imageUrl.isBlank()) images.add(imageUrl);
        images.addAll(additionalImageUrls);
        return List.copyOf(images);
    }
    public String getPickupLocation() { return pickupLocation; }
    public String getStatus() { return status; }
    public MarketplaceUser getSeller() { return seller; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void updateImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void markSold() { status = "SOLD"; }
}