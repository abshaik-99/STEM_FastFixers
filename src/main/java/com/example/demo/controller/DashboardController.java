package com.example.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;


import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.service.UserService;
import com.example.demo.models.User;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/customer-dashboard")
    public String customerDashboard() {
        return "customer-dashboard";
    }

    @GetMapping("/handyman-dashboard")
    public String handymanDashboard() {
        return "handyman-dashboard";
    }

    @GetMapping("/handymen")
    public String handymen() {
        return "handymen";
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userService.getUserByEmail(email);

            if (user != null) {
                model.addAttribute("user", user);

                // Redirect based on user role
                if (user.getRole().equalsIgnoreCase("Customer")) {
                    return "customerProfile";  // Renders customerProfile.html
                } else if (user.getRole().equalsIgnoreCase("Handyman")) {
                    return "handymanProfile";  // Renders handymanProfile.html
                }
            }
        }

        return "redirect:/login"; // If not authenticated, redirect to login
    }

}
