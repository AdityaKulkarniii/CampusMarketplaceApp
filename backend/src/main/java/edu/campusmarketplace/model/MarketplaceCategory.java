package edu.campusmarketplace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "marketplace_categories")
public class MarketplaceCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String name;
    protected MarketplaceCategory() { }
    public MarketplaceCategory(String name) { this.name = name; }
    public Long getId() { return id; }
    public String getName() { return name; }
}