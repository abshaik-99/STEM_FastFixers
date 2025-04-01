package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.User;
import com.example.demo.response.LoginResponse;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserLoginService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.example.demo.DTO.HandymanDTO;
import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.UserProfileDTO;
import com.example.demo.exception.UserAlreadyExistsException;

@RestController
@CrossOrigin
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/api/auth/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            logger.info("Received registration request for email: " + user.getEmail());
            userService.registerUser(user);
            logger.info("Registration successful for email: " + user.getEmail());
            emailService.sendInvitationEmail(user.getName(), user.getEmail());
            return ResponseEntity.ok("User registered successfully");
        } catch (UserAlreadyExistsException e) {
            logger.warning("Registration failed - user exists: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.severe("Registration failed with error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/api/auth/admin-register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody User user) {
        try {
            logger.info("Received admin registration request for email: " + user.getEmail());
            user.setRole("ADMIN");
            userService.registerUser(user);
            logger.info("Admin registration successful for email: " + user.getEmail());
            emailService.sendInvitationEmail(user.getName(), user.getEmail());
            return ResponseEntity.ok("Admin registered successfully");
        } catch (UserAlreadyExistsException e) {
            logger.warning("Admin registration failed - user exists: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.severe("Admin registration failed with error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Admin registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponse loginResponse = userLoginService.loginUser(loginDTO);
    
            // If login is successful
            if (loginResponse.isStatus()) {
                // Determine the redirect URL based on the user's role
                String redirectUrl = determineRedirectUrl(loginResponse.getUserRole());
                loginResponse.setRedirectUrl(redirectUrl);

                return ResponseEntity.ok(loginResponse);
            } else {
                // If login fails (e.g., invalid credentials)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", loginResponse.getMessage() // Use the message from LoginResponse
                        ));
            }
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "An error occurred during login. Please try again."
                    ));
        }

    }

    private String determineRedirectUrl(String userRole) {
        if ("ROLE_CUSTOMER".equalsIgnoreCase(userRole)) { // Match exact role name
            return "/customer-dashboard";
        } else if ("ROLE_HANDYMAN".equalsIgnoreCase(userRole)) {
            return "/handyman-dashboard";
        } else {
            return "/default-dashboard"; // Fallback for other roles
        }
    }

    @PostMapping("/api/auth/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginDTO loginDTO) {
        logger.info(String.format("Admin login request received for email: %s", loginDTO.getEmail()));
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Admin login successful for email: " + loginDTO.getEmail());

            // Check if the authenticated user has an admin role
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_Admin"))) {
                return ResponseEntity.ok(Map.of("success", true, "redirectUrl", "/api/admin/dashboard"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Access denied"));
            }

        } catch (UsernameNotFoundException e) {
            logger.warning("Admin not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Admin not found. Please register."));
        } catch (BadCredentialsException e) {
            logger.severe("Invalid password for email: " + loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Invalid password"));
        } catch (Exception e) {
            logger.severe("Error during admin login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Invalid credentials"));
        }
    }


    @GetMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<?> getProfile() {
        // Get the authenticated user's email from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userService.getUserByEmail(email);

            if (user != null) {
                logger.info("Fetched user: " + user.getName() + ", " + user.getEmail());

                UserProfileDTO profileDTO = new UserProfileDTO(user);

                return ResponseEntity.ok(profileDTO);  // ✅ Use the DTO instead of a custom map
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }



    @GetMapping("/api/user/me")
    public ResponseEntity<?> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(Map.of("email", userDetails.getUsername(), "roles", userDetails.getAuthorities()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    @GetMapping("/api/handymen")
    public List<HandymanDTO> getAllHandymen() {
        return userService.getAllHandymen();
    }

    @DeleteMapping("/api/user/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/api/user/delete/email/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        try {
            userService.deleteUserByEmail(email);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/api/user/delete/phone/{phoneNumber}")
    public ResponseEntity<String> deleteUserByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            userService.deleteUserByPhoneNumber(phoneNumber);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
