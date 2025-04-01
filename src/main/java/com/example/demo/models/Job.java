package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000) // Updated to match the database
    private String title;

    @Column(length = 1000) // Updated to match the database
    private String description;

    @Column(length = 255)
    private String location;

    @Column(length = 255)
    private double budget;

    @Column(length = 255)
    private String service;

    @Lob
    private byte[] fileData; // To store image/video as a byte array

    private String fileType; // To store the file type (image/video)

    private String videoPath;

    private String contactDetails;

    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }

    public String getVideoPath() { return videoPath; }

    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }

    public Long getId() { return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}