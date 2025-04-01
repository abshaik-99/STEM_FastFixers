package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DAO.UserRepository;
import com.example.demo.DTO.HandymanDTO;
import com.example.demo.models.User;
import com.example.demo.exception.UserAlreadyExistsException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public void registerUser(User user) {
        logger.info("Starting user registration process for email: " + user.getEmail());

        // Check if user exists
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warning("Email already exists: " + user.getEmail());
            throw new UserAlreadyExistsException("Email already registered");
        }
        
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            logger.warning("Phone number already exists: " + user.getPhoneNumber());
            throw new UserAlreadyExistsException("Phone number already registered");
        }

        // Validate password
        validatePassword(user.getPassword());
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        logger.info("Attempting to save user to database");
        try {
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully with ID: " + savedUser.getId());
        } catch (Exception e) {
            logger.severe("Error saving user to database: " + e.getMessage());
            throw e;
        }
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password cannot be null or empty.");
        }
    
        // Define the regex pattern for password validation
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    
        if (!password.matches(passwordPattern)) {
            throw new RuntimeException(
                "Password must be at least 8 characters long, " +
                "contain at least one digit, " +
                "one uppercase letter, " +
                "and one special character (@#$%^&+=!)."
            );
        }
    }

    public List<HandymanDTO> getAllHandymen() {
    return userRepository.findAll().stream()
            .filter(user -> "ROLE_HANDYMAN".equalsIgnoreCase(user.getRole()))
            .map(user -> new HandymanDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getRole()))
            .collect(Collectors.toList());
}

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }
    
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email " + email + " not found");
        }
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserByPhoneNumber(String phoneNumber) {
        List<User> users = userRepository.findByPhoneNumber(phoneNumber);
        
        if (users.isEmpty()) {
            throw new RuntimeException("No users found with phone number: " + phoneNumber);
        }

        userRepository.deleteAll(users);
    }

    public void recommendHandyman(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRecommended(true);
        userRepository.save(user);
    }
}
