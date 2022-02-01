package com.simulator.account.business.web.controller.utils;

public class AccountControllerUtils {
    public static String getTransactionRequestJson() {
        return """
                {
                    "amount":500
                }
            """;
    }
}
