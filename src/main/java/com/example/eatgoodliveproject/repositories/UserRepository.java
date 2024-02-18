package com.example.eatgoodliveproject.repositories;


import com.example.eatgoodliveproject.dto.ProfileUpdateDto;
import com.example.eatgoodliveproject.enums.ChatStatus;
import com.example.eatgoodliveproject.enums.CountryCode;
import com.example.eatgoodliveproject.model.Address;
import com.example.eatgoodliveproject.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface UserRepository extends JpaRepository<Users, Long> {


    @Modifying
    @Query("UPDATE Users u SET u.fullName = :fullName, u.username = :username, u.profilePictureUrl = :profilePictureUrl, "  +
            "u.phoneNumber = :phoneNumber, u.addresses = :addresses, u.city = :city, u.country = :country WHERE u.id = :id")
    void partialUpdate(@Param("id") Long id, @Param("fullName") String fullName, @Param("username") String username, @Param("profilePictureUrl") String profilePictureUrl,  @Param("phoneNumber") String phoneNumber, @Param("addresses")Address addresses, @Param("city") String city, @Param("country") String country);
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);

}
