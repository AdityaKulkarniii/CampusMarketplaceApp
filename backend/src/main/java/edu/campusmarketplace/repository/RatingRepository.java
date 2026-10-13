package edu.campusmarketplace.repository;

import edu.campusmarketplace.model.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RatingRepository extends JpaRepository<SellerRating, Long> {
    boolean existsByProductIdAndRaterId(Long productId, Long raterId);
    long countBySellerId(Long sellerId);
    @Query("select coalesce(avg(r.score), 0) from SellerRating r where r.seller.id = :sellerId")
    double averageForSeller(Long sellerId);
    @Transactional
    void deleteAllByProductId(Long productId);
}