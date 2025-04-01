package com.example.demo.service;

import com.example.demo.models.User; // Add this import statement
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.DAO.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    
        // Convert "Customer" -> "ROLE_CUSTOMER", "Handyman" -> "ROLE_HANDYMAN"
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole(); // e.g., "ROLE_Customer" or "ROLE_Handyman"
        logger.info("User found: {}, Role: {}", user.getEmail(), role);
    
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }

    public Optional<User> authenticateUser(String email, String password) {
        return userRepository.findOneByEmailAndPassword(email, password);
    }
}
