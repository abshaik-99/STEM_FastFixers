package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.models.EmailRequest;
import com.example.demo.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:5500") // Allow front-end origin
public class EmailController {

	@Autowired
    private EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) { 
	    emailService.sendEmail(
			"stemfastfixers@gmail.com",
	        request.getName(),
	        request.getEmail(),
	        request.getPhone(),
	        request.getSubject(),
	        request.getMessage()
	    );
	    return ResponseEntity.ok("✅ Email sent successfully!");
	}
}
