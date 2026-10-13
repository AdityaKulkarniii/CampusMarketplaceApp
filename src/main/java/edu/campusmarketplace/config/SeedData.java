package edu.campusmarketplace.config;

import edu.campusmarketplace.model.MarketplaceCategory;
import edu.campusmarketplace.model.MarketplaceUser;
import edu.campusmarketplace.model.Product;
import edu.campusmarketplace.repository.CategoryRepository;
import edu.campusmarketplace.repository.ProductRepository;
import edu.campusmarketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seedMarketplace(UserRepository users, ProductRepository products, CategoryRepository categories,
                                      PasswordEncoder passwordEncoder, @Value("${app.admin.email}") String adminEmail,
                                      @Value("${app.admin.password}") String adminPassword, Environment environment) {
        return args -> {
            List.of("Textbooks", "Electronics", "Furniture", "Clothing", "Supplies", "Other").forEach(name -> {
                if (!categories.existsByNameIgnoreCase(name)) categories.save(new MarketplaceCategory(name));
            });
            if (!users.existsByEmail(adminEmail.toLowerCase())) {
                users.save(new MarketplaceUser("Campus Admin", adminEmail, passwordEncoder.encode(adminPassword), "Campus Marketplace", "ADMIN"));
            }
            if (environment.acceptsProfiles(Profiles.of("local"))) {
                MarketplaceUser maya = users.findByEmail("maya@campus.edu").orElseGet(() -> users.save(
                    new MarketplaceUser("Maya Rivera", "maya@campus.edu", passwordEncoder.encode("Student123!"), "North Campus", "STUDENT")));
                List.of(
                    new Product("Botany: An Introduction", "Textbooks", new BigDecimal("28.00"), "Clean copy with light highlighting in two chapters. Great for the intro course.", "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=900&q=80", "Science Library steps", maya),
                    new Product("Scientific calculator", "Supplies", new BigDecimal("20.00"), "TI-36X Pro in great shape. Ideal for engineering and calculus.", "https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80", "Engineering quad", maya),
                    new Product("Calculus II study guide", "Textbooks", new BigDecimal("12.00"), "Worked examples and a formula sheet. A few pencilled notes inside.", "https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=900&q=80", "Main library cafe", maya),
                    new Product("Casio fx-991CW ClassWiz", "Supplies", new BigDecimal("24.00"), "Casio fx-991CW scientific calculator. Fully working with protective case.", "/images/casio-fx-991cw.svg", "Engineering quad", maya),
                    new Product("Data Structures and Algorithms", "Textbooks", new BigDecimal("32.00"), "CSE textbook covering arrays, trees, graphs, sorting, and algorithm analysis.", "/images/cse-data-structures.svg", "Computer Science building", maya),
                    new Product("Operating Systems: Concepts and Design", "Textbooks", new BigDecimal("30.00"), "CSE reference for processes, memory management, file systems, and concurrency.", "/images/cse-operating-systems.svg", "Computer Science building", maya),
                    new Product("Database Systems", "Textbooks", new BigDecimal("27.00"), "CSE textbook on relational databases, SQL, normalization, and transactions.", "/images/cse-database-systems.svg", "Main library cafe", maya),
                    new Product("Computer Networks", "Textbooks", new BigDecimal("29.00"), "CSE course book covering network layers, routing, protocols, and security basics.", "/images/cse-computer-networks.svg", "Computer Science building", maya),
                    new Product("A4 printer paper, 500 sheets", "Supplies", new BigDecimal("6.00"), "Unopened ream of 80 gsm A4 white printer paper.", "/images/a4-paper-ream.svg", "Campus print center", maya)
                ).forEach(product -> {
                products.findByTitleIgnoreCase(product.getTitle()).ifPresentOrElse(existing -> {
                    existing.updateImageUrl(product.getImageUrl());
                    products.save(existing);
                }, () -> products.save(product));
                });
            }
        };
    }
}