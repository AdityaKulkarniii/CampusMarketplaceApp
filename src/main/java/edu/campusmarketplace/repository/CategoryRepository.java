package edu.campusmarketplace.repository;

import edu.campusmarketplace.model.MarketplaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<MarketplaceCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
}