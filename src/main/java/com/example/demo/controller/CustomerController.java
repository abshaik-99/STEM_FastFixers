package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerController {

    @GetMapping("/jobs_posted")
    public String jobsPostedPage() {
        return "jobs_posted";
    }
}
