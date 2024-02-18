package com.example.eatgoodliveproject.utils;

import com.example.eatgoodliveproject.model.City;
import com.example.eatgoodliveproject.repositories.CityRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class CityUtils {

    private final CityRepository cityRepository;

    @Autowired
    public CityUtils(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }


    @PostConstruct
    public void readUserCSV(){

        //country database seeding
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/java/com/example/eatgoodliveproject/csv/cities.csv"))) {
            String line;
            boolean lineOne = false;
            while ((line=bufferedReader.readLine())!=null){
                String[]cities = line.split(",");
                if (lineOne) {
                    City countryDetails = City.builder()
                            .name(cities[0])
                            .country(cities[1])
                            .build();
                    cityRepository.save(countryDetails);
                }
                lineOne = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}