package com.example.eatgoodliveproject.utils;

import com.example.eatgoodliveproject.model.City;
import com.example.eatgoodliveproject.model.Country;
import com.example.eatgoodliveproject.repositories.CityRepository;
import com.example.eatgoodliveproject.repositories.CountryRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class CSVUtils {

   private final CountryRepository countryRepository;

   @Autowired
    public CSVUtils(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
   }

    @PostConstruct
    public void readUserCSV(){

        //country database seeding
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/java/com/example/eatgoodliveproject/csv/country-codes.csv"))) {
            String line;
            boolean lineOne = false;
            while ((line=bufferedReader.readLine())!=null){
                String[]country = line.split(",");
                if (lineOne) {
                    Country countryDetails = Country.builder()
                            .countryCode(country[0])
                            .country(country[1])
                            .build();
                    countryRepository.save(countryDetails);
                }
                lineOne = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}