package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.dto.ProfileUpdateDto;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateDto profileUpdateDto){
        return userService.updateProfile(userId, profileUpdateDto);
    }

}
