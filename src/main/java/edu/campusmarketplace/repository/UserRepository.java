package edu.campusmarketplace.repository;

import edu.campusmarketplace.model.MarketplaceUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<MarketplaceUser, Long> {
    Optional<MarketplaceUser> findByEmail(String email);
    boolean existsByEmail(String email);
    @EntityGraph(attributePaths = {"wishlist", "wishlist.seller", "wishlist.additionalImageUrls"})
    Optional<MarketplaceUser> findWithWishlistById(Long id);
}