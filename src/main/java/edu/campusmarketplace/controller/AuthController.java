package edu.campusmarketplace.controller;

import edu.campusmarketplace.dto.ApiModels.AuthResponse;
import edu.campusmarketplace.dto.ApiModels.LoginRequest;
import edu.campusmarketplace.dto.ApiModels.RegisterRequest;
import edu.campusmarketplace.dto.ApiModels.UserResponse;
import edu.campusmarketplace.model.MarketplaceUser;
import edu.campusmarketplace.repository.UserRepository;
import edu.campusmarketplace.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository users, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users; this.passwordEncoder = passwordEncoder; this.authenticationManager = authenticationManager; this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists.");
        MarketplaceUser user = users.save(new MarketplaceUser(request.name().trim(), email, passwordEncoder.encode(request.password()), request.campus().trim(), "STUDENT"));
        return new AuthResponse(jwtService.createToken(email), UserResponse.from(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (BadCredentialsException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        }
        MarketplaceUser user = users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect."));
        if (!user.isActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account is currently suspended.");
        return new AuthResponse(jwtService.createToken(user.getEmail()), UserResponse.from(user));
    }
}