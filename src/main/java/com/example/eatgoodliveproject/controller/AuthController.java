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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        return userService.loginUser(loginDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication, HttpServletRequest request) {
        String result = userService.logoutUser(authentication, request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        Users user = verificationToken.getUser();
        resendVerificationTokenMail(user, emailSenderService.applicationUrl(request),verificationToken);
        return "Verification link has been sent to your email";
    }

    void resendVerificationTokenMail(Users user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();

        // sendVerificationEmail()
        log.info("Click the link to verify your account: {}", url);
    }

    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")){
            return new ResponseEntity<>( "User Verified Successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Bad User", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetEmailDto passwordDto, HttpServletRequest request) throws RuntimeException {
        Users user = userService.findUserByEmail(passwordDto.getEmail());
        String url = "";
        if(user != null){
            String token =  userService.generateRandomNumber(6);
            userService.createPasswordResetTokenForUser(user, token);
            try {
                url = emailSenderService.forgetPasswordResetTokenMail(user, emailSenderService.applicationUrl(request), token);
            }catch (Exception e){
                throw new RuntimeException("Error sending mail");
            }
            return new ResponseEntity<>("Go to Email to reset Password " + url, HttpStatus.OK);
        }
        throw new RuntimeException("User with email " + passwordDto.getEmail() + "not found");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetEmailDto passwordResetEmailDto, HttpServletRequest request){
        Users user = userService.findUserByEmail(passwordResetEmailDto.getEmail());
        String url = "";
        if (user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url =emailSenderService.passwordResetTokenMail(user.getUsername(), emailSenderService.applicationUrl(request), token);

        }
        return new ResponseEntity<>("go to your mail to reset your password" + url, HttpStatus.OK);

    }


    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordDto passwordDto){
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())){
            return "Invalid Old Password";
        }
        userService.changePassword(user, passwordDto.getNewPassword());
        return "Password Change Successfully";
    }


    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestParam("token") String token,
                                               @RequestBody ResetPasswordDto passwordDto){
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")){
            return new ResponseEntity<>("Invalid Token", HttpStatus.NOT_FOUND);
        }
        Optional<Users> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()){
            userService.changePassword(user.get(), passwordDto.getNewPassword());
            return new ResponseEntity<>("Password Reset Successfully", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Invalid Token", HttpStatus.BAD_REQUEST);
        }
    }

}
