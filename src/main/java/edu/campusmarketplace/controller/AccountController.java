package edu.campusmarketplace.controller;

import edu.campusmarketplace.dto.ApiModels.NotificationResponse;
import edu.campusmarketplace.dto.ApiModels.ProductResponse;
import edu.campusmarketplace.model.MarketplaceUser;
import edu.campusmarketplace.repository.NotificationRepository;
import edu.campusmarketplace.repository.ProductRepository;
import edu.campusmarketplace.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final UserRepository users;
    private final ProductRepository products;
    private final NotificationRepository notifications;

    public AccountController(UserRepository users, ProductRepository products, NotificationRepository notifications) {
        this.users = users; this.products = products; this.notifications = notifications;
    }

    @GetMapping("/wishlist")
    @Transactional(readOnly = true)
    public List<ProductResponse> wishlist(@AuthenticationPrincipal MarketplaceUser user) {
        return users.findWithWishlistById(user.getId()).orElseThrow().getWishlist().stream().map(ProductResponse::from).toList();
    }

    @PostMapping("/wishlist/{productId}")
    @Transactional
    public ProductResponse saveProduct(@PathVariable Long productId, @AuthenticationPrincipal MarketplaceUser user) {
        MarketplaceUser account = users.findWithWishlistById(user.getId()).orElseThrow();
        var product = products.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found."));
        account.getWishlist().add(product);
        users.save(account);
        return ProductResponse.from(product);
    }

    @DeleteMapping("/wishlist/{productId}")
    @Transactional
    public void removeProduct(@PathVariable Long productId, @AuthenticationPrincipal MarketplaceUser user) {
        MarketplaceUser account = users.findWithWishlistById(user.getId()).orElseThrow();
        account.getWishlist().removeIf(product -> product.getId().equals(productId));
        users.save(account);
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> notifications(@AuthenticationPrincipal MarketplaceUser user) {
        return notifications.findByRecipientIdOrderByCreatedAtDesc(user.getId()).stream().map(NotificationResponse::from).toList();
    }
}