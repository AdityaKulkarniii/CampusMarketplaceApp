package edu.campusmarketplace.dto;

import edu.campusmarketplace.model.MarketplaceCategory;
import edu.campusmarketplace.model.MarketplaceNotification;
import edu.campusmarketplace.model.MarketplaceUser;
import edu.campusmarketplace.model.Product;
import edu.campusmarketplace.model.ProductReport;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiModels {
    private ApiModels() { }

    public record RegisterRequest(@NotBlank @Size(max = 100) String name, @NotBlank @Email @Size(max = 190) String email,
                                  @NotBlank @Size(min = 8, max = 72) String password, @NotBlank @Size(max = 120) String campus) { }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record AuthResponse(String token, UserResponse user) { }
    public record UserResponse(Long id, String name, String email, String campus, String role, double rating, int ratingCount, boolean active) {
        public static UserResponse from(MarketplaceUser user) {
            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCampus(), user.getRole(), user.getRating(), user.getRatingCount(), user.isActive());
        }
    }
    public record ProductRequest(@NotBlank @Size(max = 120) String title, @NotBlank @Size(max = 40) String category,
                                 @NotNull @DecimalMin("0.0") BigDecimal price, @NotBlank @Size(max = 1000) String description,
                                 @Size(max = 1000) String imageUrl, @NotBlank @Size(max = 180) String pickupLocation,
                                 @Pattern(regexp = "(?i)SALE|RENT") String listingType,
                                 @Pattern(regexp = "(?i)DAY|WEEK|MONTH") String rentalPeriod,
                                 @Min(1) @Max(365) Integer minimumRentalDuration,
                                 @Size(max = 7) List<@NotBlank @Size(max = 1000) String> imageUrls) { }
    public record ProductResponse(Long id, String title, String category, BigDecimal price, String listingType,
                                  String rentalPeriod, Integer minimumRentalDuration, String description,
                                  String imageUrl, List<String> imageUrls, String pickupLocation, String status, Long sellerId, String sellerName,
                                  double sellerRating, int sellerRatingCount, String campus, LocalDateTime createdAt) {
        public static ProductResponse from(Product product) {
            MarketplaceUser seller = product.getSeller();
            return new ProductResponse(product.getId(), product.getTitle(), product.getCategory(), product.getPrice(),
                    product.getListingType(), product.getRentalPeriod(), product.getMinimumRentalDuration(),
                    product.getDescription(), product.getImageUrl(), product.getImageUrls(), product.getPickupLocation(), product.getStatus(),
                    seller.getId(), seller.getName(), seller.getRating(), seller.getRatingCount(), seller.getCampus(), product.getCreatedAt());
        }
    }
    public record ContactRequest(@NotBlank @Size(max = 1000) String message) { }
    public record ReportRequest(@NotBlank @Size(max = 500) String reason) { }
    public record RatingRequest(@Min(1) @Max(5) int rating) { }
    public record NotificationResponse(Long id, String message, boolean read, LocalDateTime createdAt) {
        public static NotificationResponse from(MarketplaceNotification notification) {
            return new NotificationResponse(notification.getId(), notification.getMessage(), notification.isRead(), notification.getCreatedAt());
        }
    }
    public record ReportResponse(Long id, String reason, String productTitle, String reporterName, LocalDateTime createdAt, boolean resolved) {
        public static ReportResponse from(ProductReport report) {
            return new ReportResponse(report.getId(), report.getReason(), report.getProduct().getTitle(), report.getReporter().getName(), report.getCreatedAt(), report.isResolved());
        }
    }
    public record CategoryRequest(@NotBlank @Size(max = 40) String name) { }
    public record CategoryResponse(Long id, String name) {
        public static CategoryResponse from(MarketplaceCategory category) { return new CategoryResponse(category.getId(), category.getName()); }
    }
    public record MessageResponse(String message) { }
}