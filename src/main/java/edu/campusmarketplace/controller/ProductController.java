package edu.campusmarketplace.controller;

import edu.campusmarketplace.dto.ApiModels.ContactRequest;
import edu.campusmarketplace.dto.ApiModels.MessageResponse;
import edu.campusmarketplace.dto.ApiModels.ProductRequest;
import edu.campusmarketplace.dto.ApiModels.ProductResponse;
import edu.campusmarketplace.dto.ApiModels.RatingRequest;
import edu.campusmarketplace.dto.ApiModels.ReportRequest;
import edu.campusmarketplace.model.MarketplaceNotification;
import edu.campusmarketplace.model.MarketplaceUser;
import edu.campusmarketplace.model.Product;
import edu.campusmarketplace.model.ProductReport;
import edu.campusmarketplace.model.SellerRating;
import edu.campusmarketplace.repository.NotificationRepository;
import edu.campusmarketplace.repository.ProductRepository;
import edu.campusmarketplace.repository.RatingRepository;
import edu.campusmarketplace.repository.ReportRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository products;
    private final NotificationRepository notifications;
    private final ReportRepository reports;
    private final RatingRepository ratings;

    public ProductController(ProductRepository products, NotificationRepository notifications, ReportRepository reports, RatingRepository ratings) {
        this.products = products; this.notifications = notifications; this.reports = reports; this.ratings = ratings;
    }

    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false) String query, @RequestParam(required = false) String category,
                                      @RequestParam(defaultValue = "newest") String sort,
                                      @AuthenticationPrincipal MarketplaceUser viewer) {
        var stream = products.findAll().stream();
        if (!viewer.getRole().equals("ADMIN")) {
            stream = stream.filter(product -> product.getSeller().getCampus().equalsIgnoreCase(viewer.getCampus()));
        }
        if (query != null && !query.isBlank()) {
            String term = query.trim().toLowerCase();
            stream = stream.filter(product -> (product.getTitle() + " " + product.getDescription() + " " + product.getCategory()).toLowerCase().contains(term));
        }
        if (category != null && !category.isBlank()) stream = stream.filter(product -> product.getCategory().equalsIgnoreCase(category));
        Comparator<Product> comparator = switch (sort) {
            case "price-asc" -> Comparator.comparing(Product::getPrice);
            case "price-desc" -> Comparator.comparing(Product::getPrice).reversed();
            default -> Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        return stream.sorted(comparator).map(ProductResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id, @AuthenticationPrincipal MarketplaceUser viewer) {
        Product product = findProduct(id);
        if (!viewer.getRole().equals("ADMIN") && !product.getSeller().getCampus().equalsIgnoreCase(viewer.getCampus()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found.");
        return ProductResponse.from(product);
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request, @AuthenticationPrincipal MarketplaceUser seller) {
        String listingType = request.listingType() == null ? "SALE" : request.listingType().trim().toUpperCase();
        if (!listingType.equals("SALE") && !listingType.equals("RENT"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose whether this listing is for sale or rent.");
        String rentalPeriod = null;
        Integer minimumRentalDuration = null;
        if (listingType.equals("RENT")) {
            if (request.rentalPeriod() == null || request.minimumRentalDuration() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rental listings need a price period and minimum rental duration.");
            rentalPeriod = request.rentalPeriod().trim().toUpperCase();
            if (!List.of("DAY", "WEEK", "MONTH").contains(rentalPeriod))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rental price period must be day, week, or month.");
            minimumRentalDuration = request.minimumRentalDuration();
        }
        String imageUrl = validateImageUrl(request.imageUrl());
        List<String> additionalImageUrls = request.imageUrls() == null ? List.of() :
                request.imageUrls().stream().map(this::validateImageUrl).toList();
        Product product = new Product(request.title().trim(), request.category().trim(), request.price(), listingType,
                rentalPeriod, minimumRentalDuration, request.description().trim(), imageUrl, additionalImageUrls,
                request.pickupLocation().trim(), seller);
        return ProductResponse.from(products.save(product));
    }

    private String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        String value = imageUrl.trim();
        if (value.startsWith("/images/") && !value.contains("..") && !value.contains("\\")) return value;
        try {
            URI uri = URI.create(value);
            if (("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) &&
                    uri.getHost() != null) return value;
        } catch (IllegalArgumentException ignored) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo URLs must use http://, https://, or a local /images/ path.");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo URLs must use http://, https://, or a local /images/ path.");
    }

    @PatchMapping("/{id}/sold")
    public ProductResponse markSold(@PathVariable Long id, @AuthenticationPrincipal MarketplaceUser user) {
        Product product = findProduct(id);
        if (!product.getSeller().getId().equals(user.getId()) && !user.getRole().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the seller can mark this item as sold.");
        product.markSold();
        return ProductResponse.from(products.save(product));
    }

    @PostMapping("/{id}/contact")
    public MessageResponse contact(@PathVariable Long id, @Valid @RequestBody ContactRequest request, @AuthenticationPrincipal MarketplaceUser sender) {
        Product product = findProduct(id);
        if (product.getSeller().getId().equals(sender.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot contact yourself about your own listing.");
        if (!product.getStatus().equals("AVAILABLE")) throw new ResponseStatusException(HttpStatus.CONFLICT, "This item is no longer available.");
        notifications.save(new MarketplaceNotification(product.getSeller(), sender.getName() + " (" + sender.getEmail() + ") about " + product.getTitle() + ": " + request.message().trim()));
        return new MessageResponse("Your message was sent to the seller.");
    }

    @PostMapping("/{id}/report")
    public MessageResponse report(@PathVariable Long id, @Valid @RequestBody ReportRequest request, @AuthenticationPrincipal MarketplaceUser reporter) {
        reports.save(new ProductReport(findProduct(id), reporter, request.reason().trim()));
        return new MessageResponse("Thank you. The listing was reported for review.");
    }

    @PostMapping("/{id}/rating")
    @Transactional
    public MessageResponse rateSeller(@PathVariable Long id, @Valid @RequestBody RatingRequest request, @AuthenticationPrincipal MarketplaceUser rater) {
        Product product = findProduct(id);
        MarketplaceUser seller = product.getSeller();
        if (!product.getStatus().equals("SOLD")) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ratings are available after an item is marked sold.");
        if (seller.getId().equals(rater.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot rate yourself.");
        if (ratings.existsByProductIdAndRaterId(id, rater.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "You already rated this handoff.");
        ratings.save(new SellerRating(product, seller, rater, request.rating()));
        int count = Math.toIntExact(ratings.countBySellerId(seller.getId()));
        seller.updateRating(ratings.averageForSeller(seller.getId()), count);
        return new MessageResponse("Thanks for rating this handoff.");
    }

    private Product findProduct(Long id) {
        return products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found."));
    }
}