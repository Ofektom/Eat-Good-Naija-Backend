package com.example.eatgoodliveproject.config;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig<T> {
    @Bean
    public PagedListHolder<T> pagedListHolder(){
        PagedListHolder<T> pagedListHolder = new PagedListHolder<>();
        pagedListHolder.setPageSize(10);
        return pagedListHolder;
    }
}
