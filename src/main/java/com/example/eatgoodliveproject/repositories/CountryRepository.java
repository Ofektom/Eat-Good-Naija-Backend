package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
