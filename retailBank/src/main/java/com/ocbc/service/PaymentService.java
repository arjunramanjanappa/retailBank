package com.ocbc.service;

import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import com.ocbc.utility.PaymentUtility;
import com.ocbc.validator.InputValidation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("paymentService")
public class PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    InputValidation inputValidation;

    @Autowired
    PaymentUtility paymentUtility;

    public String transfer(String senderName, double debitAmount, String receiverName) {
        if (inputValidation.validateUserName(senderName) && inputValidation.validateUserName(receiverName)
                && !StringUtils.equalsIgnoreCase(senderName, receiverName)) {
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

            if (paymentUtility.checkEligibleForPayment(sender, debitAmount)) {

                //Settle Amount Owed by receiver
                if (paymentUtility.checkReceiverOwesDebt(sender, receiver)) {
                    return settleDebtToSender(sender, receiver, debitAmount);
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

    public String settleDebtToSender(User sender, User receiver, double debitAmount) {

        double senderBalance = sender.getBalance();
        double receiverDebtBalance = receiver.getDebtBalance();
        double receiverBalance = receiver.getBalance();

        double remainDebtAmount = receiverDebtBalance - debitAmount;

        if (remainDebtAmount >= 0) {
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

    public String sendPayment(User sender, User receiver, double debitAmount) {

        double senderBalance = sender.getBalance();
        double senderDebtBalance = sender.getDebtBalance();
        double receiverBalance = receiver.getBalance();
        double senderDebitAmount = senderBalance - debitAmount;
        if (senderDebitAmount < 0 && senderBalance >= 0 && senderDebtBalance == 0) {
            // Sender has Insufficient Balance to make payment
            double creditAmount = debitAmount + senderDebitAmount;

            double newReceiverBalance = receiverBalance + creditAmount;
            System.out.println("Transferred " + creditAmount + " to " + receiver.getFirstName());
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
            System.out.println("Transferred " + debitAmount + " to " + receiver.getFirstName());
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
            System.out.println("Transferred " + debitAmount + " to " + receiver.getFirstName());
            receiver.setBalance(newReceiverBalance);
            sender.setBalance(senderDebitAmount);
            this.userRepository.save(sender);
            this.userRepository.save(receiver);
            return "Payment successfull !! New balance is " + sender.getBalance();
        }

    }
}