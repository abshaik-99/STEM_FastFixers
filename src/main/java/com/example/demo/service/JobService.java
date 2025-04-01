package com.example.demo.service;

import com.example.demo.models.Job;

import com.example.demo.DAO.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import java.io.IOException;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = Logger.getLogger(JobService.class.getName());

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public class FileDataRetrievalException extends Exception {
        public FileDataRetrievalException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void processUploadedFile(MultipartFile file) throws IOException {
        String mimeType = file.getContentType();

        if (mimeType == null) {
            throw new IllegalArgumentException("File type cannot be determined.");
        }

        if (mimeType.startsWith("video/")) {
            // Handle video file
            logger.info("Processing video file: " + file.getOriginalFilename());
        } else if (mimeType.startsWith("image/")) {
            // Handle image file
            logger.info("Processing image file: " + file.getOriginalFilename());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + mimeType);
        }
    }

    public byte[] getFileData(Long jobId) throws FileDataRetrievalException {
        String sql = "SELECT file_data FROM job WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, jobId);
    }

    public Optional<String> getFileType(Long jobId) {
        String sql = "SELECT file_type FROM job WHERE id = ?";
        List<String> results = jdbcTemplate.query(sql, ps -> ps.setLong(1, jobId), (rs, rowNum) -> rs.getString("file_type")); 

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void saveJob(Job job, MultipartFile file) {
        String mimeType = file.getContentType();
        job.setFileType(mimeType); // Save the MIME type to the database
        jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public void updateJob(Job job) {
        Optional<Job> existingJob = jobRepository.findById(job.getId());
        if (existingJob.isPresent()) {
            Job jobToUpdate = existingJob.get();
            jobToUpdate.setTitle(job.getTitle());
            jobToUpdate.setDescription(job.getDescription());
            jobToUpdate.setLocation(job.getLocation());
            jobToUpdate.setBudget(job.getBudget());
            jobToUpdate.setService(job.getService());
            jobToUpdate.setFileData(job.getFileData());
            jobToUpdate.setFileType(job.getFileType());
            jobToUpdate.setContactDetails(job.getContactDetails());
            jobRepository.save(jobToUpdate);
            logger.info("Job updated successfully with ID: " + job.getId());
        } else {
            logger.warning("Job with ID " + job.getId() + " not found!");
            throw new RuntimeException("Job not found");
        }
    }

    public Optional<Job> getJobById(Long id) {
        Optional<Job> job = jobRepository.findById(id);
        logger.info("Fetching job with ID: " + id);
        
        if (job.isPresent()) {
            logger.info("Job found: " + job.get());
        } else {
            logger.warning("Job with ID " + id + " not found!");
        }
        
        return job;
    }

    public List<Job> searchJobs(String title, String location, String service) {
        return jobRepository.findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCaseOrServiceContainingIgnoreCase(title, location, service);
    }
}

