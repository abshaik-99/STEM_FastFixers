package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SessionController {


    @GetMapping("/check-session1")
    public ResponseEntity<Map<String, Boolean>> checkSession1(HttpSession session) {
        boolean loggedIn = session.getAttribute("userEmail") != null;
        return ResponseEntity.ok(Map.of("loggedIn", loggedIn));
    }

}
