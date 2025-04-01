package com.example.demo.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.User;

import io.micrometer.common.lang.NonNull;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(@NonNull String email);
    Optional<User> findOneByEmailAndPassword(String email, String password);
    List<User> findByPhoneNumber(String phoneNumber);
    void deleteByEmail(@NonNull String email);
    boolean existsById(@NonNull Long id);
    boolean existsByEmail(String email);  // Checks if email exists
    boolean existsByPhoneNumber(String phoneNumber);  // Checks if phone number exists
}
