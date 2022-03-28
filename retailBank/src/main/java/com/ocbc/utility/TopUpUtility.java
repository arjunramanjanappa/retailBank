package com.ocbc.utility;

import com.ocbc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("topUpUtility")
public class TopUpUtility {

    private static final Logger logger = LogManager.getLogger(TopUpUtility.class);

    public boolean checkUserOweDebt(User user) {
        if (StringUtils.isNotEmpty(user.getPayDebtTo())) {
            logger.info("User Owes Debt");
            return true;
        }
        return false;
    }

}
