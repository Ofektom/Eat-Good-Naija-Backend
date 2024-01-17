package com.example.eatgoodliveproject.serviceimpl;



import com.example.eatgoodliveproject.enums.Roles;
import com.example.eatgoodliveproject.model.Address;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.service.UserService;
import org.springframework.stereotype.Service;

import org.example.eatgoodliveprojectpersonal.dto.SignupDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.function.Function;


@Service
public class UserServiceImpl implements UserDetailsService, UserService {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+username));
    }


    @Override
    public Users saveAdmin(SignupDto signupDto) {
        Address address = new Address();
        address.setDescription("Orchid Road, USA");
        Address address1 = new Address();
        address1.setDescription("Lekki, Jamaica");
        Users user = new ObjectMapper().convertValue(signupDto, Users.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserRole(Roles.VENDOR);
        user.setCity("NA");
        user.setAddresses(List.of(address, address1));
        user.setCountry("NA");
        user.setProfilePictureUrl("NA");
        return userRepository.save(user);
    }

    @Override
    public Users saveUser(SignupDto signupDto) {
        Address address = new Address();
        address.setDescription("Trinity Road, USA");
        Address address1 = new Address();
        address1.setDescription("Malingo, Jamaica");
        Users user = new ObjectMapper().convertValue(signupDto, Users.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserRole(Roles.CUSTOMER);
        user.setCity("NA");
        user.setAddresses(List.of(address, address1));
        user.setCountry("NA");
        user.setProfilePictureUrl("NA");
        return userRepository.save(user);
    }
}
