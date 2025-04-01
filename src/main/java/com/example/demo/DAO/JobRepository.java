package com.example.demo.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.example.demo.models.Job;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCaseOrServiceContainingIgnoreCase(
    String title, String location, String service);

}
