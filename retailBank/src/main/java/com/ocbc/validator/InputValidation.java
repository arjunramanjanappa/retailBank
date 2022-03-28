package com.ocbc.validator;


import org.springframework.stereotype.Component;

@Component("inputValidation")
public class InputValidation {
    public boolean validateUserName(String firstName) {
        if ("".equalsIgnoreCase(firstName) || "\"\"".equalsIgnoreCase(firstName)) {
            return false;
        }
        return true;
    }
}
