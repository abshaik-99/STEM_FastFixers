package com.example.demo.service;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.response.LoginResponse;

public interface UserLoginService {

    public LoginResponse loginUser(LoginDTO loginDTO);

}
