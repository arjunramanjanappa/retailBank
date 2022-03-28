package com.ocbc.validator;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("inputValidation")
public class InputValidation {
    private static final Logger logger = LogManager.getLogger(InputValidation.class);
    public boolean validateUserName(String firstName) {
        if ("".equalsIgnoreCase(firstName) || "\"\"".equalsIgnoreCase(firstName)) {
            logger.info("Input Validation Failed !!");
            return false;
        }
        logger.info("Input Validation Success !!");;
        return true;
    }
}
