package com.example.demo.models;

import jakarta.persistence.*;

@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;
    private String bidderName;
    private Double bidAmount;
    private Integer deliveryDays;

    // Constructors
    public Bid() {}
    
    public Bid(Long jobId, String bidderName, Double bidAmount, Integer deliveryDays) {
        this.jobId = jobId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
        this.deliveryDays = deliveryDays;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public String getBidderName() { return bidderName; }
    public Double getBidAmount() { return bidAmount; }
    public Integer getDeliveryDays() { return deliveryDays; }

    public void setId(Long id) { this.id = id; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public void setBidderName(String bidderName) { this.bidderName = bidderName; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }
    public void setDeliveryDays(Integer deliveryDays) { this.deliveryDays = deliveryDays; }
}

