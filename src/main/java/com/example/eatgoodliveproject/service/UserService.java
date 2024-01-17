package com.example.eatgoodliveproject.service;


import org.example.eatgoodliveprojectpersonal.dto.SignupDto;
import com.example.eatgoodliveproject.model.Users;


public interface UserService {
    Users saveAdmin(SignupDto signupDto);

    Users saveUser(SignupDto signupDto);
}
