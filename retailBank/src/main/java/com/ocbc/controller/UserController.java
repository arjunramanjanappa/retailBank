package com.ocbc.controller;

import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("users")
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }


    @RequestMapping("/user/login")
    public String userLogin(@RequestParam(required = true) String firstName) {
        if (validateUserName(firstName)) {
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


    @RequestMapping("/user/topup")
    public String userTopUp(@RequestParam(required = true) String firstName,
                            @RequestParam(required = true) double topupAmount) {

        if (validateUserName(firstName)) {
            List<User> UserList = this.userRepository.findAll();
            List<User> existingUser = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(firstName)).collect(Collectors.toList());
            if (existingUser.isEmpty()) {
                return "Invalid User";
            } else {
                // Existing User
                User user = existingUser.get(0);
                if (checkUserOweDebt(user)) {
                    return settleUserDebt(user, topupAmount);
                } else {
                    return TopUpUser(user, topupAmount);
                }
            }
        } else {
            return "Invalid Transaction Details";
        }
    }


    @RequestMapping("/user/payment")
    public String transfer(@RequestParam(required = true) String senderName,
                            @RequestParam(required = true) double debitAmount,
                            @RequestParam(required = true) String receiverName) {

        if (validateUserName(senderName) && validateUserName(receiverName) && !StringUtils.equalsIgnoreCase(senderName,receiverName)) {
            List<User> UserList = this.userRepository.findAll();
            List<User> senderList = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(senderName)).collect(Collectors.toList());
            List<User> receiverList = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(receiverName)).collect(Collectors.toList());
            if (senderList.isEmpty()) {
                return "Invalid Sender";
            }
            if (receiverList.isEmpty()) {
                return "Invalid Receiver";
            }

            // Existing User
            User sender = senderList.get(0);
            User receiver = receiverList.get(0);

            if (checkEligibleForPayment(sender, debitAmount)) {

                //Settle Amount Owed by receiver
                if(checkReceiverOwesDebt(sender,receiver)){
                    return settleDebtToSender (sender, receiver, debitAmount);
                } else {
                    // Make Payment to receiver
                    return sendPayment(sender, receiver, debitAmount);
                }
            } else {
                return "Insufficient Balance";
            }
        }
        return "Invalid Payment Details - Transaction Failed";
    }

    public boolean validateUserName(String firstName) {
        if ("".equalsIgnoreCase(firstName) || "\"\"".equalsIgnoreCase(firstName)) {
            return false;
        }
        return true;
    }


    public String sendPayment(User sender, User receiver, double debitAmount) {

        double senderBalance = sender.getBalance();
        double senderDebtBalance = sender.getDebtBalance();
        double receiverBalance = receiver.getBalance();
        double senderDebitAmount = senderBalance - debitAmount;
        if (senderDebitAmount < 0 && senderBalance >= 0 && senderDebtBalance == 0) {
            // Sender has Insufficient Balance to make payment
            double creditAmount = debitAmount + senderDebitAmount;

            double newReceiverBalance = receiverBalance + creditAmount;
            System.out.println("Transferred "+creditAmount+" to " + receiver.getFirstName());
            receiver.setBalance(newReceiverBalance);
            receiver.setOweDebtFrom(sender.getFirstName());

            sender.setBalance(0);
            sender.setPayDebtTo(receiver.getFirstName());
            sender.setDebtBalance(senderDebitAmount * -1);

            this.userRepository.save(sender);
            this.userRepository.save(receiver);
            return "Payment successfull !! New balance is " + sender.getBalance();
        } else if (senderDebitAmount == 0 && senderBalance >= 0 && senderDebtBalance == 0) {
            // Sender has exact Balance to make payment

            double newReceiverBalance = receiverBalance + debitAmount;
            System.out.println("Transferred "+debitAmount+" to " + receiver.getFirstName());
            receiver.setBalance(newReceiverBalance);

            sender.setBalance(0);
            sender.setPayDebtTo("");
            sender.setDebtBalance(0);

            this.userRepository.save(sender);
            this.userRepository.save(receiver);
            return "Payment successfull !! New balance is " + sender.getBalance();
        } else {
            // Sender has sufficient Balance to make payment
            double newReceiverBalance = receiverBalance + debitAmount;
            System.out.println("Transferred "+debitAmount+" to " + receiver.getFirstName());
            receiver.setBalance(newReceiverBalance);
            sender.setBalance(senderDebitAmount);
            this.userRepository.save(sender);
            this.userRepository.save(receiver);
            return "Payment successfull !! New balance is " + sender.getBalance();
        }

    }

    public boolean checkEligibleForPayment(User sender, double debitAmount) {

        double balance = sender.getBalance();
        if (balance <= 0) {
            // if Sender has in sufficient Balance
            return false;
        } else if (balance > 0 && StringUtils.isNotEmpty(sender.getPayDebtTo())) {
            // if Sender has sufficient Balance to make payment even though he has debt
            return true;
        }
        return true;
    }


    public boolean checkUserOweDebt(User user) {
        if (StringUtils.isNotEmpty(user.getPayDebtTo())) {
            return true;
        }
        return false;
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
            System.out.println("Transferred "+topupAmount+" to " + payDebtToUser.getFirstName());
            payDebtToUser.setBalance(newReceiverBalance);
            this.userRepository.save(payDebtToUser);
            user.setDebtBalance(debtBalance - topupAmount);
            this.userRepository.save(user);
            return "Top up successfull !! Your balance is " + user.getBalance();
        } else if (debtBalance - topupAmount == 0) {
            // user debt cleared
            double newReceiverBalance = payDebtToUser.getBalance() + topupAmount;
            System.out.println("Transferred "+topupAmount+" to " + payDebtToUser.getFirstName());
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
            System.out.println("Transferred "+user.getDebtBalance()+" to " + payDebtToUser.getFirstName());
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

    public boolean checkReceiverOwesDebt(User sender, User receiver){

        if(StringUtils.isNotEmpty(sender.getOweDebtFrom()) &&
                StringUtils.equalsIgnoreCase(sender.getOweDebtFrom(),receiver.getFirstName())){
            return true;
        }
        return false;

    }

    public String settleDebtToSender(User sender, User receiver, double debitAmount) {

        double senderBalance = sender.getBalance();
        double receiverDebtBalance = receiver.getDebtBalance();
        double receiverBalance = receiver.getBalance();

        double remainDebtAmount = receiverDebtBalance - debitAmount;

        if(remainDebtAmount >= 0){
            receiver.setDebtBalance(remainDebtAmount);
            this.userRepository.save(receiver);
        } else {
            //settle debt and update new amount to receiver
            receiver.setDebtBalance(0);
            receiver.setPayDebtTo("");
            double receiverNewBalance = receiverBalance + (remainDebtAmount * -1);
            receiver.setBalance(receiverNewBalance);
            this.userRepository.save(receiver);

            double senderNewBalance = senderBalance - remainDebtAmount;
            sender.setBalance(senderNewBalance);
            this.userRepository.save(sender);
        }
        return "Payment successfull !! New balance is " + sender.getBalance();

    }
}
