package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.User;
import com.example.demo.models.Job;
import com.example.demo.service.UserService;
import com.example.demo.service.JobService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @GetMapping("/dashboard")
    public String adminDashboardPage() {
        return "admin";
    }

    @GetMapping("/load-content")
    public String loadContent(@RequestParam String page) {
        switch (page) {
            case "users":
                return "admin/manageUsers";
            case "jobs":
                return "admin/manageJobs";
            case "bids":
                return "admin/manageBids";
            case "recommended":
                return "admin/recommendedHandymen";
            default:
                return "error-page";
        }
    }

    // CRUD operations for users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // CRUD operations for jobs
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        jobService.saveJob(job, null);
        return ResponseEntity.ok(job);
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job) {
        job.setId(id);
        jobService.updateJob(job);
        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    // Mark handyman as recommended
    @PutMapping("/handymen/{id}/recommend")
    public ResponseEntity<Map<String, String>> recommendHandyman(@PathVariable Long id) {
        userService.recommendHandyman(id);
        return ResponseEntity.ok(Map.of("message", "Handyman recommended successfully"));
    }
}
