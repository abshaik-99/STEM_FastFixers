package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import com.example.demo.models.Job;
import com.example.demo.service.JobService;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobService jobService;

    // Get all jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> createJob(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("budget") double budget,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            // Validate required fields
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Title is required."));
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Description is required."));
            }
            if (description.length() > 1000) {
                return ResponseEntity.badRequest().body(Map.of("message", "Description is too long. Please limit it to 1000 characters."));
            }
            if (location == null || location.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Location is required."));
            }
            if (budget <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Budget must be greater than 0."));
            }

            Job job = new Job();
            job.setTitle(title);
            job.setDescription(description);
            job.setLocation(location);
            job.setBudget(budget);

            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid file type. Only images and videos are allowed."));
                }
                job.setFileData(file.getBytes());
                job.setFileType(file.getContentType());
            }

            jobService.saveJob(job, file);
            return ResponseEntity.ok(Map.of("message", "Job posted successfully!"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error processing file upload."));
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Job posting failed: " + e.getMessage()));
        }
    }

    // Delete a job by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok("Job deleted successfully.");
    }

    // Get a job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Optional<Job> job = jobService.getJobById(id);
        return job.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<Resource> getVideoFile(@PathVariable Long id) {
        Optional<Job> jobOptional = jobService.getJobById(id);
        if (jobOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Job job = jobOptional.get();

        if (job.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(job.getFileData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(job.getFileType()))
                .body(resource);
    }

    // Search jobs by title, location, or service
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String service) {
    
    List<Job> jobs = jobService.getAllJobs();

    if (title != null && !title.isEmpty()) {
        jobs = jobs.stream()
                .filter(job -> job.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    if (location != null && !location.isEmpty()) {
        jobs = jobs.stream()
                .filter(job -> job.getLocation().equalsIgnoreCase(location))
                .toList();
    }

    if (service != null && !service.isEmpty()) {
        jobs = jobs.stream()
                .filter(job -> job.getService().equalsIgnoreCase(service))
                .toList();
    }

    return ResponseEntity.ok(jobs);
}


    // Serve the "Find a Job" page
    @GetMapping("/findjob")
    public String findJobPage() {
        return "findAJob";
    }

    // Serve the "Post a Job" page
    @GetMapping("/postjob")
    public String postJobPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login"; // Redirect to login if not authenticated
        }
        return "postAJob"; // Return the post-job page
    }
    
    // Serve the "Jobs Posted" page
    @GetMapping("/jobs_posted")
    public String jobsPostedPage() {
        return "jobs_posted";
    }

    

    // Get job by ID (HTML page)
    @GetMapping("/description/{id}")
    public String getJobPageById(@PathVariable Long id, Model model) {
        Optional<Job> job = jobService.getJobById(id);
        if (job.isPresent()) {
            model.addAttribute("job", job.get());
            return "jobDescription";
        } else {
            model.addAttribute("errorMessage", "Invalid job ID");
            return "error-page"; // Redirect to an error page
        }
    }


    @GetMapping("/jobList")
    public String jobListedPage() {
        return "jobList";
    }

    @GetMapping("/appliedJobs")
    public String appliedJobsPage() {
        return "appliedJobs";
    }
}
