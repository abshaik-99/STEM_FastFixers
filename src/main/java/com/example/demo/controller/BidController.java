package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.Bid;
import com.example.demo.service.BidService;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "http://localhost:5500")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitBid(@RequestBody Bid bid) {
        bidService.submitBid(bid);
        return ResponseEntity.ok("Bid submitted successfully");
    }

    @GetMapping("/user/{bidderName}")
    public ResponseEntity<List<Bid>> getUserBids(@PathVariable String bidderName) {
        List<Bid> bids = bidService.getUserBids(bidderName);
        return ResponseEntity.ok(bids);
    }
}

