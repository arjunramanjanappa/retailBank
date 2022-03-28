package com.ocbc.controller;

import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import com.ocbc.service.LoginService;
import com.ocbc.service.PaymentService;
import com.ocbc.service.TopUpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("users")
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }


    @RequestMapping("/user/login")
    public String userLogin(@RequestParam(required = true) String firstName) throws Exception {
        try {
            LoginService loginService = new LoginService();
            return loginService.login(firstName);
        } catch (Exception e) {
            logger.error("Error Occured",e.getMessage());
            throw new Exception("We are unable to Login. Please try again later");
        }
    }


    @RequestMapping("/user/topup")
    public String userTopUp(@RequestParam(required = true) String firstName,
                            @RequestParam(required = true) double topupAmount) throws Exception {
        try {
            TopUpService topUpService = new TopUpService();
            return topUpService.topUpBalance(firstName, topupAmount);
        }catch (Exception e){
            logger.error("Error Occured",e.getMessage());
            throw new Exception("We are unable process your request. Please try again later");
        }
    }


    @RequestMapping("/user/payment")
    public String transfer(@RequestParam(required = true) String senderName,
                           @RequestParam(required = true) double debitAmount,
                           @RequestParam(required = true) String receiverName) throws Exception {
        try{
            PaymentService paymentService = new PaymentService();
            return paymentService.transfer(senderName, debitAmount, receiverName);
        }catch (Exception e){
            logger.error("Error Occured",e.getMessage());
            throw new Exception("We are unable process your request. Please try again later");
        }
    }
}
