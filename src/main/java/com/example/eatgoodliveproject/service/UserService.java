package com.example.eatgoodliveproject.service;


import com.example.eatgoodliveproject.dto.LoginDto;
import com.example.eatgoodliveproject.dto.ProfileUpdateDto;
import com.example.eatgoodliveproject.dto.SignupDto;
import jakarta.servlet.http.HttpServletRequest;
import com.example.eatgoodliveproject.model.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;


public interface UserService {
    Users saveAdmin(SignupDto signupDto);

    Users saveUser(SignupDto signupDto);

    ResponseEntity<String> loginUser(LoginDto loginDto);

    String logoutUser(Authentication authentication, HttpServletRequest request);

    ResponseEntity<String> updateProfile(Long UserId, ProfileUpdateDto profileUpdateDto);
}
