package com.ocbc.service;

import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import com.ocbc.validator.InputValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("loginService")
public class LoginService {

    private static final Logger logger = LogManager.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    InputValidation inputValidation;

    public String login(String firstName){
        logger.info("Login as "+firstName);

        if (inputValidation.validateUserName(firstName)) {
            List<User> UserList = this.userRepository.findAll();
            List<User> existingUser = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(firstName)).collect(Collectors.toList());
            if (existingUser.isEmpty()) {
                //Register New User
                logger.info("Register New User");
                this.userRepository.save(new User(firstName, 0, "", 0, ""));
                logger.info("User Saved");
                return "User Saved";
            } else {
                // Existing User
                logger.info("Existing User detected");
                logger.info("Welcome Back " + firstName);
                return "Welcome Back " + firstName;
            }
        } else {
            logger.error("Invalid User");
            return "Invalid User";
        }

    }

}
