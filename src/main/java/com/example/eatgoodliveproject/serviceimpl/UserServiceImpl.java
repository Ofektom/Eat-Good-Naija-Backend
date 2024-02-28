package com.example.eatgoodliveproject.serviceimpl;


import com.example.eatgoodliveproject.dto.LoginDto;
import com.example.eatgoodliveproject.dto.ProfileUpdateDto;
import com.example.eatgoodliveproject.dto.SignupDto;
import com.example.eatgoodliveproject.enums.Roles;
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

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final JwtUtils jwtUtils;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private VerificationTokenRepository verificationTokenRepository;


    @Autowired
    public UserServiceImpl(JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder,
                           PasswordResetTokenRepository passwordResetTokenRepository, VerificationTokenRepository verificationTokenRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+username));
    }


    @Override
    public Users saveAdmin(SignupDto signupDto) {
        Address address = new Address();
        address.setDescription("NA");
        Users user = new ObjectMapper().convertValue(signupDto, Users.class);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Email is already taken, try Logging In or Signup with another email" );
        }

//        if (user.isPasswordMatching()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));
            user.setUserRole(Roles.VENDOR);
            user.setCity("NA");
            user.setAddresses(List.of(address));
            user.setCountry("NA");
            user.setProfilePictureUrl("NA");
            return userRepository.save(user);
//        } else {
//            throw new RuntimeException("Passwords do not Match!");
//        }

    }

    @Override
    public Users saveUser(SignupDto signupDto) {
        Address address = new Address();
        address.setDescription("NA");
        Users user = new ObjectMapper().convertValue(signupDto, Users.class);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Email is already taken, try Logging In or Signup with another email" );
        }

//        if (user.isPasswordMatching()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));
            user.setUserRole(Roles.CUSTOMER);
            user.setCity("NA");
            user.setAddresses(List.of(address));
            user.setCountry("NA");
            user.setProfilePictureUrl("NA");
            return userRepository.save(user);
//        } else {
//            throw new RuntimeException("Passwords do not Match!");
//        }

    }

    @Override
    public ResponseEntity<String> loginUser(LoginDto loginDto) {
        UserDetails user = loadUserByUsername(loginDto.getUsername());
        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            String token = jwtUtils.createJwt.apply(user);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>("Username or Password not correct!", HttpStatus.BAD_REQUEST);
    }

    @Override
    public String logoutUser(Authentication authentication, HttpServletRequest request) {
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();
            return "User logged Out Successfully";
        } else {
            return "User not authenticated";
        }
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

    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
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
    public void changePassword(Users user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean checkIfValidOldPassword(Users user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
    public Optional<Users> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
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
