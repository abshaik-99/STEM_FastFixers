package com.example.demo.service.IMPL;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.models.User;
import com.example.demo.service.UserLoginService;
import com.example.demo.response.LoginResponse;
import com.example.demo.DAO.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserImpl implements UserLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse loginUser(LoginDTO loginDTO) {
        User user1 = userRepository.findByEmail(loginDTO.getEmail());
    
        if (user1 != null) {
            String password = loginDTO.getPassword();
            String hashedPassword = user1.getPassword();
    
            Boolean isPasswordMatch = passwordEncoder.matches(password, hashedPassword);
            if (isPasswordMatch) {
                // Instead of querying again, use the already fetched user1
                return new LoginResponse("Login Successful", true, user1.getRole());
            } else {
                return new LoginResponse("Password not matched", false, null);
            }
        } else {
            return new LoginResponse("Email doesn't exist", false, null);
        }
    }

}
