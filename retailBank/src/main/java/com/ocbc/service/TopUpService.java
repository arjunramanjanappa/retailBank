package com.ocbc.service;

import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import com.ocbc.utility.TopUpUtility;
import com.ocbc.validator.InputValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("topUpService")
public class TopUpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    InputValidation inputValidation;

    @Autowired
    TopUpUtility topUpUtility;


    public String topUpBalance(String firstName, double topupAmount){
        if (inputValidation.validateUserName(firstName)) {
            List<User> UserList = this.userRepository.findAll();
            List<User> existingUser = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(firstName)).collect(Collectors.toList());
            if (existingUser.isEmpty()) {
                return "Invalid User";
            } else {
                // Existing User
                User user = existingUser.get(0);
                if (topUpUtility.checkUserOweDebt(user)) {
                    return settleUserDebt(user, topupAmount);
                } else {
                    return TopUpUser(user, topupAmount);
                }
            }
        } else {
            return "Invalid Transaction Details";
        }
    }
    public String settleUserDebt(User user, double topupAmount) {
        List<User> UserList = this.userRepository.findAll();
        List<User> payDebtToUserList = UserList.stream().filter(user1 -> user1.getFirstName().equalsIgnoreCase(user.getPayDebtTo())).collect(Collectors.toList());
        User payDebtToUser = payDebtToUserList.get(0);
        // calculate debt to be credited
        double debtBalance = user.getDebtBalance();

        if (debtBalance - topupAmount > 0) {
            // user still in debt
            double newReceiverBalance = payDebtToUser.getBalance() + topupAmount;
            System.out.println("Transferred " + topupAmount + " to " + payDebtToUser.getFirstName());
            payDebtToUser.setBalance(newReceiverBalance);
            this.userRepository.save(payDebtToUser);
            user.setDebtBalance(debtBalance - topupAmount);
            this.userRepository.save(user);
            return "Top up successfull !! Your balance is " + user.getBalance();
        } else if (debtBalance - topupAmount == 0) {
            // user debt cleared
            double newReceiverBalance = payDebtToUser.getBalance() + topupAmount;
            System.out.println("Transferred " + topupAmount + " to " + payDebtToUser.getFirstName());
            payDebtToUser.setBalance(newReceiverBalance);
            payDebtToUser.setOweDebtFrom("");
            this.userRepository.save(payDebtToUser);
            user.setDebtBalance(0);
            user.setPayDebtTo("");
            this.userRepository.save(user);
            return "Top up successfull !! Your balance is " + user.getBalance();
        } else {
            // user debt cleared and add extra to main balance
            double newReceiverBalance = payDebtToUser.getBalance() + user.getDebtBalance();
            System.out.println("Transferred " + user.getDebtBalance() + " to " + payDebtToUser.getFirstName());
            payDebtToUser.setBalance(newReceiverBalance);
            payDebtToUser.setOweDebtFrom("");
            this.userRepository.save(payDebtToUser);
            user.setBalance(topupAmount - user.getDebtBalance());
            user.setPayDebtTo("");
            user.setDebtBalance(0);
            this.userRepository.save(user);
            return "Top up successfull !! Your balance is " + user.getBalance();
        }

    }

    public String TopUpUser(User user, double topupAmount) {
        double newBalance = user.getBalance() + topupAmount;
        user.setBalance(newBalance);
        this.userRepository.save(user);
        return "Top up successfull !! Your balance is " + user.getBalance();
    }
}
