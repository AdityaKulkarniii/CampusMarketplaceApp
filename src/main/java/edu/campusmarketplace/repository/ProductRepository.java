package edu.campusmarketplace.repository;

import edu.campusmarketplace.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
	boolean existsByTitleIgnoreCase(String title);
	Optional<Product> findByTitleIgnoreCase(String title);

	@Override
	@EntityGraph(attributePaths = {"seller", "additionalImageUrls"})
	List<Product> findAll();

	@Override
	@EntityGraph(attributePaths = {"seller", "additionalImageUrls"})
	Optional<Product> findById(Long id);
}