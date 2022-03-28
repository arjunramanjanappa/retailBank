package com.ocbc.utility;

import com.ocbc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component("topUpUtility")
public class TopUpUtility {


    public boolean checkUserOweDebt(User user) {
        if (StringUtils.isNotEmpty(user.getPayDebtTo())) {
            return true;
        }
        return false;
    }

}
