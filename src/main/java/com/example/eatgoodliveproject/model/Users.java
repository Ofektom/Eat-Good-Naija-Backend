package com.example.eatgoodliveproject.model;




import com.example.eatgoodliveproject.enums.ChatStatus;
import com.example.eatgoodliveproject.enums.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Size(min = 4, message = "Name must be at least 4 characters")
    private String fullName;

    @Email(message = "Please enter a valid email address")
    @NotEmpty(message = "Email cannot be empty")
    @Column(unique = true, nullable = false)
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password must be at least 8 characters and include one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @Transient
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Confirm password must match the password")
    private String confirmPassword;

    @JsonIgnore
    private String profilePictureUrl;

//    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
//    @Pattern(regexp = "^\\d{10}$", message = "Mobile Number must contain only Numbers")

    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;

    @JsonIgnore
    private boolean enabled = false;


    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Product> products;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "user_address", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<Address> addresses = new ArrayList<>();

    @JsonIgnore
    private String city;

    @JsonIgnore
    private String country;

    @Enumerated(value = EnumType.STRING)
    private Roles userRole;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Orders> orders;

    public Users(String fullName, String username, String phoneNumber, String password, String confirmPassword, boolean enabled, Roles userRole){
        this.fullName = fullName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.enabled = enabled;
        this.userRole = userRole;
    }



    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority(this.userRole.name())));
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

//    @JsonIgnore
//    @AssertTrue(message = "Passwords do not match")
//    public boolean isPasswordMatching(){
//        return password != null && password.equals(confirmPassword);
//    }
}
