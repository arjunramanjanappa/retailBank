package com.ocbc.utility;

import com.ocbc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("paymentUtility")
public class PaymentUtility {

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

    public boolean checkReceiverOwesDebt(User sender, User receiver) {

        if (StringUtils.isNotEmpty(sender.getOweDebtFrom()) &&
                StringUtils.equalsIgnoreCase(sender.getOweDebtFrom(), receiver.getFirstName())) {
            return true;
        }
        return false;

    }
}
