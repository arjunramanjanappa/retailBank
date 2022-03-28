package com.ocbc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetailBankApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RetailBankApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //this.userRepository.save(new User("Alice",210,"",0 , "Bob"));
        //this.userRepository.save(new User("Bob",0,"Alice",40, ""));
    }
}
