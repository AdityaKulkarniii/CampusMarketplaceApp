package edu.campusmarketplace.repository;

import edu.campusmarketplace.model.ProductReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface ReportRepository extends JpaRepository<ProductReport, Long> {
    @EntityGraph(attributePaths = {"product", "reporter"})
    List<ProductReport> findByResolvedFalseOrderByCreatedAtDesc();
    @Transactional
    void deleteAllByProductId(Long productId);
}