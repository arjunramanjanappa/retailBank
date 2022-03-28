package com.ocbc.service;

import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import com.ocbc.validator.InputValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("loginService")
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    InputValidation inputValidation;

    public String login(String firstName){

        if (inputValidation.validateUserName(firstName)) {
            List<User> UserList = this.userRepository.findAll();
            List<User> existingUser = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(firstName)).collect(Collectors.toList());
            if (existingUser.isEmpty()) {
                //Register New User
                this.userRepository.save(new User(firstName, 0, "", 0, ""));
                return "User Saved";
            } else {
                // Existing User
                return "Welcome Back " + firstName;
            }
        } else {
            return "Invalid User";
        }

    }

}
