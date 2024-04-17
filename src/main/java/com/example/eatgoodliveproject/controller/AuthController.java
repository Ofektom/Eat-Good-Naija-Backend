package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.dto.*;
import com.example.eatgoodliveproject.event.RegistrationCompleteEvent;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.model.VerificationToken;
import com.example.eatgoodliveproject.serviceimpl.EmailSenderService;
import com.example.eatgoodliveproject.serviceimpl.UserServiceImpl;
import com.example.eatgoodliveproject.utils.GoogleJwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private UserServiceImpl userService;
    private GoogleJwtUtils googleJwtUtils;
    private ApplicationEventPublisher publisher;
    private EmailSenderService emailSenderService;


    @Autowired
    public AuthController(UserServiceImpl userService, GoogleJwtUtils googleJwtUtils, ApplicationEventPublisher publisher,EmailSenderService emailSenderService) {
        this.userService = userService;
        this.googleJwtUtils = googleJwtUtils;
        this.publisher = publisher;
        this.emailSenderService = emailSenderService;
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
    public ResponseEntity<String> saveUpAdmin(@RequestBody SignupDto signupDto, final HttpServletRequest request){
        Users user = userService.saveAdmin(signupDto);
        publisher.publishEvent(new RegistrationCompleteEvent(user,emailSenderService.applicationUrl(request)));
        return new ResponseEntity<>("Signup successful, go to your mail to verify your account", HttpStatus.OK);
    }

    @PostMapping("/customer-sign-up")
    public ResponseEntity<String> signUpUser(@RequestBody SignupDto signupDto, final HttpServletRequest request){
        Users user = userService.saveUser(signupDto);
        publisher.publishEvent(new RegistrationCompleteEvent(user, emailSenderService.applicationUrl(request)));
        return new ResponseEntity<>("Signup successful, go to your mail to verify your account", HttpStatus.OK);
    }

    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")){
            return new ResponseEntity<>( "User Verified Successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("User not verified", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        return userService.loginUser(loginDto);
    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(Authentication authentication, HttpServletRequest request) {
//        String result = userService.logoutUser(authentication, request);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        String result = userService.logoutUser(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @PostMapping("/changePassword")
//    public ResponseEntity <String> changePassword(@RequestBody ChangePasswordDto passwordDto) {
//        return new ResponseEntity<>(userService.changeUserPassword(passwordDto), HttpStatus.OK);
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetEmailDto passwordDto, HttpServletRequest request){
        userService.forgotPassword(passwordDto, request);
        return new ResponseEntity<>("Forgot password email successfully sent", HttpStatus.OK);

    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody ResetPasswordDto passwordDto) {
        return userService.resetPassword(token, passwordDto);
    }

}
