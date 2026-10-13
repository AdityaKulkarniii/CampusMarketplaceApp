package edu.campusmarketplace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "marketplace_users")
public class MarketplaceUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, unique = true, length = 190)
    private String email;
    @JsonIgnore
    @Column(nullable = false, length = 100)
    private String passwordHash;
    @Column(nullable = false, length = 120)
    private String campus;
    @Column(nullable = false, length = 16)
    private String role = "STUDENT";
    @Column(nullable = false)
    private double rating = 0.0;
    @Column(nullable = false)
    private int ratingCount = 0;
    @Column(nullable = false)
    private boolean active = true;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_wishlist", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> wishlist = new HashSet<>();

    protected MarketplaceUser() { }

    public MarketplaceUser(String name, String email, String passwordHash, String campus, String role) {
        this.name = name;
        this.email = email.toLowerCase().trim();
        this.passwordHash = passwordHash;
        this.campus = campus;
        this.role = role;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_" + role)); }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return passwordHash; }
    @Override public boolean isEnabled() { return active; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCampus() { return campus; }
    public String getRole() { return role; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }
    public boolean isActive() { return active; }
    public Set<Product> getWishlist() { return wishlist; }
    public void setActive(boolean active) { this.active = active; }
    public void updateRating(double rating, int ratingCount) { this.rating = rating; this.ratingCount = ratingCount; }
}