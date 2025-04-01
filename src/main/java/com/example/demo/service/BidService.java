package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DAO.BidRepository;
import com.example.demo.models.Bid;

@Service
public class BidService {
    
    @Autowired
    private BidRepository bidRepository;

    public void submitBid(Bid bid) {
        bidRepository.save(bid);
    }

    public List<Bid> getUserBids(String bidderName) {
        return bidRepository.findByBidderName(bidderName);
    }
}


