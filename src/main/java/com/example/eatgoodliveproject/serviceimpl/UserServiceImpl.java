package com.example.eatgoodliveproject.serviceimpl;


import com.example.eatgoodliveproject.dto.*;
import com.example.eatgoodliveproject.enums.Roles;
import com.example.eatgoodliveproject.exception.*;
import com.example.eatgoodliveproject.model.Address;
import com.example.eatgoodliveproject.model.PasswordResetToken;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.model.VerificationToken;
import com.example.eatgoodliveproject.repositories.PasswordResetTokenRepository;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.repositories.VerificationTokenRepository;
import com.example.eatgoodliveproject.service.UserService;
import com.example.eatgoodliveproject.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final JwtUtils jwtUtils;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private final EmailSenderService emailSenderService;


    @Autowired
    public UserServiceImpl(JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder,
                           PasswordResetTokenRepository passwordResetTokenRepository, VerificationTokenRepository verificationTokenRepository, EmailSenderService emailSenderService) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailSenderService = emailSenderService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+username));
    }


    @Override
    public Users saveAdmin(SignupDto signupDto) {
        if (userRepository.existsByUsername(signupDto.getUsername())) {
            throw new EmailIsTakenException("Email is already taken, try Logging In or Signup with another email" );
        }
        Users user = new Users();

        if (!signupDto.getPassword().equals (signupDto.getConfirmPassword())){
            throw new PasswordsDontMatchException("Passwords are not the same");
        }
        if (!validatePassword(signupDto.getPassword())) {
            throw new PasswordsDontMatchException("Password does not meet the required criteria");
        }
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(signupDto.getConfirmPassword()));
        user.setFullName(signupDto.getFullName());
        user.setPhoneNumber(signupDto.getPhoneNumber());
        user.setUsername(signupDto.getUsername());
        user.setUserRole(Roles.VENDOR);
        user.setCountry(signupDto.getCountry());
        return userRepository.save(user);
    }

    @Override
    public Users saveUser(SignupDto signupDto) {
        if (userRepository.existsByUsername(signupDto.getUsername())) {
            throw new EmailIsTakenException("Email is already taken, try Logging In or Signup with another email" );
        }
        Users user = new Users();

        if (!signupDto.getPassword().equals (signupDto.getConfirmPassword())){
            throw new PasswordsDontMatchException("Passwords are not the same");
        }
        if (!validatePassword(signupDto.getPassword())) {
            throw new PasswordsDontMatchException("Password does not meet the required criteria");
        }
            user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
            user.setConfirmPassword(passwordEncoder.encode(signupDto.getConfirmPassword()));
            user.setFullName(signupDto.getFullName());
            user.setPhoneNumber(signupDto.getPhoneNumber());
            user.setUsername(signupDto.getUsername());
            user.setUserRole(Roles.CUSTOMER);
            user.setCountry(signupDto.getCountry());
            return userRepository.save(user);
    }

    public boolean validatePassword(String password){
        String regex = "^.*(?=.{8,})(?=...*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    @Override
    public ResponseEntity<String> loginUser(LoginDto loginDto) {
        UserDetails user = loadUserByUsername(loginDto.getUsername());

        if (!user.isEnabled()) {
            throw new UserNotVerifiedException("User is not verified, check email to Verify Registration");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UserNotVerifiedException("Username and Password is Incorrect");
        }

        return ResponseEntity.ok(jwtUtils.createJwt.apply(user));
    }

//    @Override
//    public String logoutUser(Authentication authentication, HttpServletRequest request) {
//        if (authentication != null) {
//            SecurityContextHolder.getContext().setAuthentication(null);
//            SecurityContextHolder.clearContext();
//            request.getSession().invalidate();
//            return "User logged Out Successfully";
//        } else {
//            return "User not authenticated";
//        }
//    }

    @Override
    public String logoutUser(HttpServletRequest request) {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "User logged out Successfully";
    }


    public void saveVerificationTokenForUser(Users user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);

    }


    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null){
            return "invalid";
        }
        Users user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <=0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public void forgotPassword(PasswordResetEmailDto passwordDto, HttpServletRequest request) {
        Users user = findUserByEmail(passwordDto.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User with email " + passwordDto.getEmail() + " not found");
        }
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        emailSenderService.passwordResetTokenMail(user.getUsername(), emailSenderService.applicationUrl(request), token);
    }

    private Optional<Users> getUserByPasswordReset(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    private void changePassword(Users user, String newPassword, String newConfirmPassword) {

        if (newPassword.equals(newConfirmPassword)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setConfirmPassword(passwordEncoder.encode(newConfirmPassword));
            userRepository.save(user);
        } else {
            throw new PasswordsDontMatchException("Passwords do not Match!");
        }
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, ResetPasswordDto passwordDto) {
        String result = validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            throw new InvalidTokenException("Invalid Token");
        }
        Optional<Users> user = getUserByPasswordReset(token);
        if (user.isPresent()) {
            changePassword(user.get(), passwordDto.getNewPassword(), passwordDto.getNewConfirmPassword());
            return new ResponseEntity<>("Password Reset Successful", HttpStatus.OK);
        } else {
            throw new InvalidTokenException("Invalid Token");
        }
    }


    public void createPasswordResetTokenForUser(Users user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null){
            return "invalid";
        }
        Users user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <=0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        return "valid";

    }


    public Users findUserByEmail(String username) {

        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username Not Found" + username));
    }





    public String generateRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Generates a random digit between 0 and 9
            stringBuilder.append(digit);
        }

        return stringBuilder.toString();
    }


    @Override
    public ResponseEntity<String> updateProfile(Long userId, ProfileUpdateDto profileUpdateDto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user with ID " + userId + " is not found"));

        List<Address> addressList = new ArrayList<>();
        for(Address address : user.getAddresses()){
            if(profileUpdateDto.getAddress() != null) {
                address.setDescription(profileUpdateDto.getAddress());
            }
            addressList.add(address);
        }

        if(profileUpdateDto.getFullName() != null) {
            user.setFullName(profileUpdateDto.getFullName());
        }
        if(profileUpdateDto.getUsername() != null) {
            user.setUsername(profileUpdateDto.getUsername());
        }
        if(profileUpdateDto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(profileUpdateDto.getProfilePictureUrl());
        }

        if(profileUpdateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(profileUpdateDto.getPhoneNumber());
        }
        user.setAddresses(addressList);
        if(profileUpdateDto.getCity() != null) {
            user.setCity(profileUpdateDto.getCity());
        }
        if(profileUpdateDto.getCountry() !=null) {
            user.setCountry(profileUpdateDto.getCountry());
        }

        userRepository.save(user);

        return new ResponseEntity<>("profile update successfully", HttpStatus.OK);
    }


}
