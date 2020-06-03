package com.example.vote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class VoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoteApplication.class, args);
    }

}
