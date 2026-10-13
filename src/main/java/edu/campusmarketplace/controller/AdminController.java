package edu.campusmarketplace.controller;

import edu.campusmarketplace.dto.ApiModels.CategoryRequest;
import edu.campusmarketplace.dto.ApiModels.CategoryResponse;
import edu.campusmarketplace.dto.ApiModels.ProductResponse;
import edu.campusmarketplace.dto.ApiModels.ReportResponse;
import edu.campusmarketplace.dto.ApiModels.UserResponse;
import edu.campusmarketplace.model.MarketplaceCategory;
import edu.campusmarketplace.model.MarketplaceUser;
import edu.campusmarketplace.repository.CategoryRepository;
import edu.campusmarketplace.repository.ProductRepository;
import edu.campusmarketplace.repository.RatingRepository;
import edu.campusmarketplace.repository.ReportRepository;
import edu.campusmarketplace.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ReportRepository reports;
    private final ProductRepository products;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final RatingRepository ratings;

    public AdminController(ReportRepository reports, ProductRepository products, UserRepository users,
                           CategoryRepository categories, RatingRepository ratings) {
        this.reports = reports; this.products = products; this.users = users; this.categories = categories; this.ratings = ratings;
    }

    @GetMapping("/reports")
    public List<ReportResponse> reports() { return reports.findByResolvedFalseOrderByCreatedAtDesc().stream().map(ReportResponse::from).toList(); }

    @PatchMapping("/reports/{id}/resolve")
    public void resolveReport(@PathVariable Long id) {
        var report = reports.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found."));
        report.resolve();
        reports.save(report);
    }

    @GetMapping("/products")
    public List<ProductResponse> products() { return products.findAll().stream().map(ProductResponse::from).toList(); }

    @DeleteMapping("/products/{id}")
    @Transactional
    public void removeProduct(@PathVariable Long id) {
        var product = products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found."));
        reports.deleteAllByProductId(id);
        ratings.deleteAllByProductId(id);
        users.findAll().forEach(user -> user.getWishlist().removeIf(saved -> saved.getId().equals(id)));
        products.delete(product);
    }

    @GetMapping("/users")
    public List<UserResponse> users() { return users.findAll().stream().map(UserResponse::from).toList(); }

    @PatchMapping("/users/{id}/toggle")
    public UserResponse toggleUser(@PathVariable Long id, @AuthenticationPrincipal MarketplaceUser administrator) {
        var user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        if (user.getId().equals(administrator.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot suspend your own account.");
        user.setActive(!user.isActive());
        return UserResponse.from(users.save(user));
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() { return categories.findAll().stream().map(CategoryResponse::from).toList(); }

    @PostMapping("/categories")
    public CategoryResponse addCategory(@Valid @RequestBody CategoryRequest request) {
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCase(name)) throw new ResponseStatusException(HttpStatus.CONFLICT, "That category already exists.");
        return CategoryResponse.from(categories.save(new MarketplaceCategory(name)));
    }

    @DeleteMapping("/categories/{id}")
    public void removeCategory(@PathVariable Long id) {
        var category = categories.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        categories.delete(category);
    }
}