package com.example.eatgoodliveproject.controller;


import org.example.eatgoodliveprojectpersonal.dto.SignupDto;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.serviceimpl.UserServiceImpl;
import com.example.eatgoodliveproject.utils.GoogleJwtUtils;
import com.example.eatgoodliveproject.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {


    private UserServiceImpl userService;
    private JwtUtils jwtUtils;
    private GoogleJwtUtils googleJwtUtils;
    private PasswordEncoder passwordEncoder;




    @Autowired
    public AuthController(UserServiceImpl userService, JwtUtils jwtUtils, GoogleJwtUtils googleJwtUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.googleJwtUtils = googleJwtUtils;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/google/{tkn}")
    public ResponseEntity<String> authorizeOauthUser(@PathVariable("tkn") String token){
        return ResponseEntity.ok(googleJwtUtils.googleOauthUserJWT(token));


    }


    @GetMapping("/dashboard")
    public String index(){
        return "Welcome to your account";
    }


    @PostMapping("/vendor-sign-up")
    public ResponseEntity<SignupDto> signUpAdmin(@RequestBody SignupDto signupDto){
        Users user = userService.saveAdmin(signupDto);
        SignupDto signupDto1 = new ObjectMapper().convertValue(user, SignupDto.class);
        return new ResponseEntity<>(signupDto1, HttpStatus.OK);
    }

    @PostMapping("/customer-sign-up")
    public ResponseEntity<SignupDto> signUpUser(@RequestBody SignupDto signupDto){
        Users user = userService.saveUser(signupDto);
        SignupDto signupDto1 = new ObjectMapper().convertValue(user, SignupDto.class);
        return new ResponseEntity<>(signupDto1, HttpStatus.OK);
    }


}
